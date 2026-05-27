package com.campeggio.checkins.service;

import com.campeggio.checkins.dto.*;
import com.campeggio.checkins.entity.*;
import com.campeggio.checkins.repository.CheckInRepository;
import com.campeggio.exceptions.*;
import com.campeggio.reservations.entity.*;
import com.campeggio.reservations.repository.PrenotazioneRepository;
import com.campeggio.users.entity.Staff;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepo;
    private final PrenotazioneRepository prenotazioneRepo;

    @Transactional
    public CheckInRecordDTO registraCheckIn(CheckInRequest req, Staff staff) {
        Prenotazione prenotazione = prenotazioneRepo.findById(req.getPrenotazioneId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prenotazione non trovata con id: " + req.getPrenotazioneId()));

        if (prenotazione.getStato() != StatoPrenotazione.CONFERMATA)
            throw new ConflictException("Il check-in è possibile solo per prenotazioni CONFERMATE");

        if (checkInRepo.findByPrenotazioneIdAndTipo(req.getPrenotazioneId(), TipoCheckIn.CHECK_IN).isPresent())
            throw new ConflictException("Il check-in per questa prenotazione è già stato registrato");

        CheckInRecord record = new CheckInRecord();
        record.setPrenotazione(prenotazione);
        record.setRegistratoDa(staff);
        record.setTipo(TipoCheckIn.CHECK_IN);
        record.setNote(req.getNote());

        return CheckInRecordDTO.from(checkInRepo.save(record));
    }

    @Transactional
    public CheckInRecordDTO registraCheckOut(CheckInRequest req, Staff staff) {
        Prenotazione prenotazione = prenotazioneRepo.findById(req.getPrenotazioneId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prenotazione non trovata con id: " + req.getPrenotazioneId()));

        checkInRepo.findByPrenotazioneIdAndTipo(req.getPrenotazioneId(), TipoCheckIn.CHECK_IN)
                .orElseThrow(() -> new ConflictException(
                        "Impossibile fare check-out: il check-in non è stato ancora registrato"));

        if (checkInRepo.findByPrenotazioneIdAndTipo(req.getPrenotazioneId(), TipoCheckIn.CHECK_OUT).isPresent())
            throw new ConflictException("Il check-out per questa prenotazione è già stato registrato");

        // Segna la prenotazione come completata
        prenotazione.setStato(StatoPrenotazione.COMPLETATA);
        prenotazioneRepo.save(prenotazione);

        CheckInRecord record = new CheckInRecord();
        record.setPrenotazione(prenotazione);
        record.setRegistratoDa(staff);
        record.setTipo(TipoCheckIn.CHECK_OUT);
        record.setNote(req.getNote());

        return CheckInRecordDTO.from(checkInRepo.save(record));
    }

    public Page<CheckInRecordDTO> getAll(Pageable pageable) {
        return checkInRepo.findAll(pageable).map(CheckInRecordDTO::from);
    }
}
