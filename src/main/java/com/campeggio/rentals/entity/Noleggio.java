package com.campeggio.rentals.entity;

import com.campeggio.users.entity.Ospite;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "noleggio")
@Getter
@Setter
@NoArgsConstructor
public class Noleggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ospite_id", nullable = false)
    private Ospite ospite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articolo_id", nullable = false)
    private ArticoloNoleggio articolo;

    @Column(nullable = false)
    private LocalDate dataInizio;

    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(nullable = false)
    private Integer quantita;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoNoleggio stato = StatoNoleggio.ATTIVO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void calcolaPrezzo() {
        if (articolo != null && dataInizio != null && dataFine != null && quantita != null) {
            long giorni = ChronoUnit.DAYS.between(dataInizio, dataFine);
            if (giorni <= 0) giorni = 1;
            totalPrice = articolo.getPrezzoGiornaliero()
                    .multiply(BigDecimal.valueOf(giorni))
                    .multiply(BigDecimal.valueOf(quantita));
        }
    }
}
