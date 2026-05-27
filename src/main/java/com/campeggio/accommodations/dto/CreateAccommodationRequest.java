package com.campeggio.accommodations.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccommodationRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    private String name;

    private String description;

    @NotNull(message = "Il prezzo per notte è obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo non può essere negativo")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "La capacità minima è 1")
    private int maxCapacity;

    @NotBlank(message = "Il tipo è obbligatorio")
    @Pattern(regexp = "PIAZZOLA|BUNGALOW|PIAZZOLA_FISSA",
            message = "Tipo deve essere PIAZZOLA, BUNGALOW o PIAZZOLA_FISSA")
    private String type;

    // Campi specifici Piazzola
    private String tipoPiazzola;   // CAMPER | TENDA | CARAVAN
    private Double surfaceM2;
    private Boolean hasElectricity;
    private Boolean hasWater;

    // Campi specifici Bungalow
    private Integer rooms;
    private Integer beds;
    private Boolean hasBathroom;
    private Boolean hasKitchen;

    // Campi specifici PiazzolaFissa
    private BigDecimal annualFee;
    private Boolean hasPrivateEntrance;
    private String contractStart;  // ISO date: "2026-01-01"
    private String contractEnd;
    private Long ownerId;
}
