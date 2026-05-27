package com.campeggio.accommodations.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bungalow")
@DiscriminatorValue("BUNGALOW")
@Getter @Setter @NoArgsConstructor
public class Bungalow extends Accommodation {

    private int rooms;
    private int beds;
    private boolean hasBathroom;
    private boolean hasKitchen;
}
