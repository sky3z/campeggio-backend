package com.campeggio.bar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "voce_ordine")
@Getter
@Setter
@NoArgsConstructor
public class VoceOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordine_id", nullable = false)
    private OrdineBar ordine;

    @Column(nullable = false, length = 100)
    private String nomeArticolo;

    @Column(nullable = false)
    private Integer quantita;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal prezzoUnitario;
}
