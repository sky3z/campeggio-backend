package com.campeggio.rentals.repository;

import com.campeggio.rentals.entity.ArticoloNoleggio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticoloNoleggioRepository extends JpaRepository<ArticoloNoleggio, Long> {

    List<ArticoloNoleggio> findByAttivoTrue();
}
