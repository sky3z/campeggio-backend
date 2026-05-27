package com.campeggio.checkins.repository;

import com.campeggio.checkins.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckInRecord, Long> {

    Optional<CheckInRecord> findByPrenotazioneIdAndTipo(Long prenotazioneId, TipoCheckIn tipo);

    Page<CheckInRecord> findByTipo(TipoCheckIn tipo, Pageable pageable);
}
