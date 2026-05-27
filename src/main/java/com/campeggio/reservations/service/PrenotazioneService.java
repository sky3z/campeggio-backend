package com.campeggio.reservations.service;

import com.campeggio.accommodations.entity.*;
import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.email.service.EmailService;
import com.campeggio.exceptions.*;
import com.campeggio.reservations.dto.*;
import com.campeggio.reservations.entity.*;
import com.campeggio.reservations.repository.PrenotazioneRepository;
import com.campeggio.users.entity.Ospite;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepo;
    private final AccommodationRepository accommodationRepo;
    private final EmailService emailService;

    public PrenotazioneDTO create(CreatePrenotazioneRequest req, Ospite ospite) {
        if (!req.getCheckOutDate().isAfter(req.getCheckInDate()))
            throw new IllegalArgumentException("Il check-out deve essere successivo al check-in");

        Accommodation acc = accommodationRepo.findById(req.getAccommodationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alloggio non trovato con id: " + req.getAccommodationId()));

        if (acc.getStatus() != AccommodationStatus.DISPONIBILE)
            throw new ConflictException("L'alloggio non è disponibile nelle date selezionate");

        Prenotazione p = new Prenotazione();
        p.setOspite(ospite);
        p.setAccommodation(acc);
        p.setCheckInDate(req.getCheckInDate());
        p.setCheckOutDate(req.getCheckOutDate());

        return PrenotazioneDTO.from(prenotazioneRepo.save(p));
    }

    public Page<PrenotazioneDTO> getAll(Pageable pageable) {
        return prenotazioneRepo.findAll(pageable).map(PrenotazioneDTO::from);
    }

    public Page<PrenotazioneDTO> getByOspite(Long ospiteId, Pageable pageable) {
        return prenotazioneRepo.findByOspiteId(ospiteId, pageable).map(PrenotazioneDTO::from);
    }

    public PrenotazioneDTO getById(Long id) {
        return prenotazioneRepo.findById(id)
                .map(PrenotazioneDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata con id: " + id));
    }

    public PrenotazioneDTO confirm(Long id) {
        Prenotazione p = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata con id: " + id));
        if (p.getStato() != StatoPrenotazione.PENDING)
            throw new ConflictException("Solo le prenotazioni in stato PENDING possono essere confermate");
        p.setStato(StatoPrenotazione.CONFERMATA);
        PrenotazioneDTO saved = PrenotazioneDTO.from(prenotazioneRepo.save(p));

        // Invia email di conferma
        Ospite ospite = p.getOspite();
        emailService.sendBookingConfirmation(
                ospite.getEmail(),
                ospite.getName() + " " + ospite.getSurname(),
                p.getId(),
                p.getAccommodation().getName(),
                p.getCheckInDate().toString(),
                p.getCheckOutDate().toString());

        return saved;
    }

    public PrenotazioneDTO cancel(Long id) {
        Prenotazione p = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata con id: " + id));
        if (p.getStato() == StatoPrenotazione.COMPLETATA)
            throw new ConflictException("Non è possibile cancellare una prenotazione completata");
        p.setStato(StatoPrenotazione.CANCELLATA);
        return PrenotazioneDTO.from(prenotazioneRepo.save(p));
    }
}
