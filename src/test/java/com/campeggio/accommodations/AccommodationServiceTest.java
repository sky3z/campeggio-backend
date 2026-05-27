package com.campeggio.accommodations;

import com.campeggio.accommodations.dto.*;
import com.campeggio.accommodations.entity.*;
import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.accommodations.service.AccommodationService;
import com.campeggio.exceptions.ResourceNotFoundException;
import com.campeggio.users.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock private AccommodationRepository accommodationRepo;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AccommodationService service;

    // ─── GET ALL ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll: restituisce pagina di alloggi come DTO")
    void getAll_restituisceLista() {
        // arrange
        Piazzola p = piazzolaFake();
        Pageable pageable = PageRequest.of(0, 10);
        when(accommodationRepo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(p)));

        // act
        Page<AccommodationDTO> result = service.getAll(pageable);

        // assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Piazzola C1");
        assertThat(result.getContent().get(0).getType()).isEqualTo("PIAZZOLA");
    }

    @Test
    @DisplayName("getAll: nessun alloggio restituisce pagina vuota")
    void getAll_listaVuota() {
        // arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(accommodationRepo.findAll(pageable)).thenReturn(Page.empty());

        // act
        Page<AccommodationDTO> result = service.getAll(pageable);

        // assert
        assertThat(result.getContent()).isEmpty();
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: alloggio esistente viene restituito correttamente")
    void getById_trovato() {
        // arrange
        Piazzola p = piazzolaFake();
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(p));

        // act
        AccommodationDTO dto = service.getById(1L);

        // assert
        assertThat(dto.getName()).isEqualTo("Piazzola C1");
        assertThat(dto.getStatus()).isEqualTo("DISPONIBILE");
    }

    @Test
    @DisplayName("getById: alloggio non trovato lancia ResourceNotFoundException")
    void getById_nonTrovato_lanceEccezione() {
        // arrange
        when(accommodationRepo.findById(99L)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── UPDATE STATUS ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStatus: stato aggiornato a MANUTENZIONE")
    void updateStatus_success() {
        // arrange
        Piazzola p = piazzolaFake();
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(p));
        when(accommodationRepo.save(any())).thenReturn(p);

        // act
        AccommodationDTO dto = service.updateStatus(1L, AccommodationStatus.MANUTENZIONE);

        // assert
        assertThat(dto.getStatus()).isEqualTo("MANUTENZIONE");
    }

    @Test
    @DisplayName("updateStatus: alloggio non trovato lancia ResourceNotFoundException")
    void updateStatus_alloggioNonTrovato_lanceEccezione() {
        // arrange
        when(accommodationRepo.findById(99L)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> service.updateStatus(99L, AccommodationStatus.DISPONIBILE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── FIND AVAILABLE ───────────────────────────────────────────────────────

    @Test
    @DisplayName("findAvailable: date invertite lanciano IllegalArgumentException")
    void findAvailable_dateInvertite_lanceEccezione() {
        // arrange
        var checkIn = java.time.LocalDate.now().plusDays(10);
        var checkOut = java.time.LocalDate.now().plusDays(5);

        // act & assert
        assertThatThrownBy(() -> service.findAvailable(checkIn, checkOut, 2, "PIAZZOLA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: alloggio esistente viene eliminato")
    void delete_success() {
        // arrange
        when(accommodationRepo.existsById(1L)).thenReturn(true);

        // act
        service.delete(1L);

        // assert
        verify(accommodationRepo).deleteById(1L);
    }

    @Test
    @DisplayName("delete: alloggio non trovato lancia ResourceNotFoundException")
    void delete_nonTrovato_lanceEccezione() {
        // arrange
        when(accommodationRepo.existsById(99L)).thenReturn(false);

        // act & assert
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(accommodationRepo, never()).deleteById(any());
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private Piazzola piazzolaFake() {
        Piazzola p = new Piazzola();
        p.setName("Piazzola C1");
        p.setDescription("Piazzola per camper");
        p.setPricePerNight(BigDecimal.valueOf(28.00));
        p.setMaxCapacity(4);
        p.setStatus(AccommodationStatus.DISPONIBILE);
        p.setTipoPiazzola(Piazzola.TipoPiazzola.CAMPER);
        p.setSurfaceM2(45.0);
        p.setHasElectricity(true);
        p.setHasWater(true);
        return p;
    }
}
