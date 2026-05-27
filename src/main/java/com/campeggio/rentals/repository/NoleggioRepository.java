package com.campeggio.rentals.repository;

import com.campeggio.rentals.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NoleggioRepository extends JpaRepository<Noleggio, Long> {

    Page<Noleggio> findByOspiteId(Long ospiteId, Pageable pageable);

    List<Noleggio> findByStatoAndDataFineBefore(StatoNoleggio stato, LocalDate date);
}
