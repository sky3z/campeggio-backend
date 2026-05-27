package com.campeggio.rentals.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "articolo_noleggio")
@Getter
@Setter
@NoArgsConstructor
public class ArticoloNoleggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descrizione;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal prezzoGiornaliero;

    @Column(nullable = false)
    private Integer quantitaDisponibile;

    @Column(nullable = false)
    private boolean attivo = true;
}
