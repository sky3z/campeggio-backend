package com.campeggio.accommodations.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "piazzola")
@DiscriminatorValue("PIAZZOLA")
@Getter @Setter @NoArgsConstructor
public class Piazzola extends Accommodation {

    public enum TipoPiazzola { CAMPER, TENDA, CARAVAN }

    @Enumerated(EnumType.STRING)
    private TipoPiazzola tipoPiazzola;

    private Double surfaceM2;
    private boolean hasElectricity;
    private boolean hasWater;
}
