package com.campeggio.reservations.repository;

import com.campeggio.reservations.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    Page<Prenotazione> findByOspiteId(Long ospiteId, Pageable pageable);

    List<Prenotazione> findByStatoAndCheckOutDateBefore(StatoPrenotazione stato, LocalDate date);

    // Conta prenotazioni attive in un periodo (per report occupazione)
    @Query("""
            SELECT COUNT(p) FROM Prenotazione p
            WHERE p.stato = 'CONFERMATA'
            AND p.checkInDate < :endDate
            AND p.checkOutDate > :startDate
            """)
    long countActiveInPeriod(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // Fatturato per mese (native SQL PostgreSQL)
    @Query(value = """
            SELECT DATE_TRUNC('month', p.check_in_date) AS mese,
                   SUM(p.total_price)                   AS totale
            FROM prenotazione p
            WHERE p.stato = 'COMPLETATA'
              AND p.check_in_date BETWEEN :start AND :end
            GROUP BY DATE_TRUNC('month', p.check_in_date)
            ORDER BY mese
            """, nativeQuery = true)
    List<Object[]> revenueByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Prenotazioni con check-in domani (per reminder email)
    @Query("""
            SELECT p FROM Prenotazione p
            WHERE p.stato = 'CONFERMATA'
            AND p.checkInDate = :tomorrow
            """)
    List<Prenotazione> findCheckInsForTomorrow(@Param("tomorrow") LocalDate tomorrow);
}
