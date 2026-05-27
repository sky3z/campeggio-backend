package com.campeggio.reservations;

import com.campeggio.accommodations.entity.*;
import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.email.service.EmailService;
import com.campeggio.exceptions.*;
import com.campeggio.reservations.dto.*;
import com.campeggio.reservations.entity.*;
import com.campeggio.reservations.repository.PrenotazioneRepository;
import com.campeggio.reservations.service.PrenotazioneService;
import com.campeggio.users.entity.Ospite;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrenotazioneServiceTest {

    @Mock private PrenotazioneRepository prenotazioneRepo;
    @Mock private AccommodationRepository accommodationRepo;
    @Mock private EmailService emailService;

    @InjectMocks
    private PrenotazioneService service;

    private Ospite ospite;
    private Piazzola piazzola;

    @BeforeEach
    void setUp() {
        ospite = new Ospite();
        ospite.setEmail("mario@example.com");
        ospite.setName("Mario");
        ospite.setSurname("Rossi");

        piazzola = new Piazzola();
        piazzola.setName("Piazzola C1");
        piazzola.setPricePerNight(BigDecimal.valueOf(28.00));
        piazzola.setMaxCapacity(4);
        piazzola.setStatus(AccommodationStatus.DISPONIBILE);
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create: prenotazione valida viene salvata e restituita")
    void create_success() {
        // arrange
        CreatePrenotazioneRequest req = new CreatePrenotazioneRequest();
        req.setAccommodationId(1L);
        req.setCheckInDate(LocalDate.now().plusDays(5));
        req.setCheckOutDate(LocalDate.now().plusDays(10));

        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(piazzola));
        when(prenotazioneRepo.save(any(Prenotazione.class))).thenAnswer(inv -> {
            Prenotazione p = inv.getArgument(0);
            p.setStato(StatoPrenotazione.PENDING);
            return p;
        });

        // act
        PrenotazioneDTO dto = service.create(req, ospite);

        // assert
        assertThat(dto).isNotNull();
        assertThat(dto.getStato()).isEqualTo("PENDING");
        verify(prenotazioneRepo).save(any(Prenotazione.class));
    }

    @Test
    @DisplayName("create: date invertite lanciano IllegalArgumentException")
    void create_dateInvertite_lanceEccezione() {
        // arrange
        CreatePrenotazioneRequest req = new CreatePrenotazioneRequest();
        req.setAccommodationId(1L);
        req.setCheckInDate(LocalDate.now().plusDays(10));
        req.setCheckOutDate(LocalDate.now().plusDays(5)); // check-out PRIMA del check-in

        // act & assert
        assertThatThrownBy(() -> service.create(req, ospite))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    @Test
    @DisplayName("create: alloggio non disponibile lancia ConflictException")
    void create_alloggioOccupato_lanceConflictException() {
        // arrange
        piazzola.setStatus(AccommodationStatus.OCCUPATA);

        CreatePrenotazioneRequest req = new CreatePrenotazioneRequest();
        req.setAccommodationId(1L);
        req.setCheckInDate(LocalDate.now().plusDays(1));
        req.setCheckOutDate(LocalDate.now().plusDays(5));

        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(piazzola));

        // act & assert
        assertThatThrownBy(() -> service.create(req, ospite))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("disponibile");
    }

    @Test
    @DisplayName("create: alloggio non trovato lancia ResourceNotFoundException")
    void create_alloggioNonTrovato_lanceNotFoundException() {
        // arrange
        CreatePrenotazioneRequest req = new CreatePrenotazioneRequest();
        req.setAccommodationId(99L);
        req.setCheckInDate(LocalDate.now().plusDays(1));
        req.setCheckOutDate(LocalDate.now().plusDays(5));

        when(accommodationRepo.findById(99L)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> service.create(req, ospite))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── CONFIRM ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("confirm: prenotazione PENDING viene confermata")
    void confirm_success() {
        // arrange
        Prenotazione p = buildPrenotazione(StatoPrenotazione.PENDING);
        when(prenotazioneRepo.findById(1L)).thenReturn(Optional.of(p));
        when(prenotazioneRepo.save(any())).thenReturn(p);
        doNothing().when(emailService).sendBookingConfirmation(any(), any(), any(), any(), any(), any());

        // act
        PrenotazioneDTO dto = service.confirm(1L);

        // assert
        assertThat(dto.getStato()).isEqualTo("CONFERMATA");
        verify(emailService).sendBookingConfirmation(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("confirm: prenotazione già CONFERMATA lancia ConflictException")
    void confirm_nonPending_lanceConflictException() {
        // arrange
        Prenotazione p = buildPrenotazione(StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepo.findById(1L)).thenReturn(Optional.of(p));

        // act & assert
        assertThatThrownBy(() -> service.confirm(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("PENDING");
    }

    // ─── CANCEL ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel: prenotazione PENDING viene cancellata")
    void cancel_success() {
        // arrange
        Prenotazione p = buildPrenotazione(StatoPrenotazione.PENDING);
        when(prenotazioneRepo.findById(1L)).thenReturn(Optional.of(p));
        when(prenotazioneRepo.save(any())).thenReturn(p);

        // act
        PrenotazioneDTO dto = service.cancel(1L);

        // assert
        assertThat(dto.getStato()).isEqualTo("CANCELLATA");
    }

    @Test
    @DisplayName("cancel: prenotazione COMPLETATA non può essere cancellata")
    void cancel_completata_lanceConflictException() {
        // arrange
        Prenotazione p = buildPrenotazione(StatoPrenotazione.COMPLETATA);
        when(prenotazioneRepo.findById(1L)).thenReturn(Optional.of(p));

        // act & assert
        assertThatThrownBy(() -> service.cancel(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("completata");
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private Prenotazione buildPrenotazione(StatoPrenotazione stato) {
        Prenotazione p = new Prenotazione();
        p.setOspite(ospite);
        p.setAccommodation(piazzola);
        p.setCheckInDate(LocalDate.now().plusDays(5));
        p.setCheckOutDate(LocalDate.now().plusDays(10));
        p.setStato(stato);
        p.setTotalPrice(BigDecimal.valueOf(140.00));
        return p;
    }
}
