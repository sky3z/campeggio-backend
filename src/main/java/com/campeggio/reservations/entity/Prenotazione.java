package com.campeggio.reservations.entity;

import com.campeggio.accommodations.entity.Accommodation;
import com.campeggio.users.entity.Ospite;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "prenotazione")
@Getter @Setter @NoArgsConstructor
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ospite_id", nullable = false)
    private Ospite ospite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione stato = StatoPrenotazione.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void calculateTotal() {
        if (checkInDate != null && checkOutDate != null && accommodation != null) {
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            this.totalPrice = accommodation.getPricePerNight()
                    .multiply(BigDecimal.valueOf(Math.max(nights, 1)));
        }
    }
}
