package com.campeggio.accommodations.repository;

import com.campeggio.accommodations.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    // Query disponibilità: trova alloggi NON prenotati nelle date indicate
    @Query("""
            SELECT a FROM Accommodation a
            WHERE a.status = 'DISPONIBILE'
            AND a.maxCapacity >= :capacity
            AND a.type = :type
            AND a.id NOT IN (
                SELECT p.accommodation.id FROM Prenotazione p
                WHERE p.stato NOT IN ('CANCELLATA')
                AND p.checkInDate < :checkOut
                AND p.checkOutDate > :checkIn
            )
            ORDER BY a.pricePerNight ASC
            """)
    List<Accommodation> findAvailable(@Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut,
                                      @Param("capacity") int capacity,
                                      @Param("type") String type);

    // Contratti annuali in scadenza entro N giorni
    @Query("""
            SELECT pf FROM PiazzolaFissa pf
            WHERE pf.contractEnd BETWEEN :today AND :limitDate
            ORDER BY pf.contractEnd ASC
            """)
    List<PiazzolaFissa> findExpiringContracts(@Param("today") LocalDate today,
                                               @Param("limitDate") LocalDate limitDate);

    List<Accommodation> findByStatus(AccommodationStatus status);

    List<Accommodation> findByPricePerNightLessThanEqual(BigDecimal maxPrice);

    List<Accommodation> findByMaxCapacityGreaterThanEqual(int capacity);
}
