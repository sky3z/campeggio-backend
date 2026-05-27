package com.campeggio.accommodations.entity;

import com.campeggio.users.entity.Ospite;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "piazzola_fissa")
@DiscriminatorValue("PIAZZOLA_FISSA")
@Getter @Setter @NoArgsConstructor
public class PiazzolaFissa extends Accommodation {

    @Column(precision = 10, scale = 2)
    private BigDecimal annualFee;

    private boolean hasPrivateEntrance;
    private LocalDate contractStart;
    private LocalDate contractEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Ospite owner;
}
