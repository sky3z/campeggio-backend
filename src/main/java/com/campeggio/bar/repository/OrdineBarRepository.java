package com.campeggio.bar.repository;

import com.campeggio.bar.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineBarRepository extends JpaRepository<OrdineBar, Long> {

    Page<OrdineBar> findByOspiteId(Long ospiteId, Pageable pageable);

    List<OrdineBar> findByStatoIn(List<StatoOrdine> stati);
}
