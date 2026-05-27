package com.campeggio.reservations.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreatePrenotazioneRequest {

    @NotNull(message = "L'ID alloggio è obbligatorio")
    private Long accommodationId;

    @NotNull(message = "La data di check-in è obbligatoria")
    @FutureOrPresent(message = "Il check-in non può essere nel passato")
    private LocalDate checkInDate;

    @NotNull(message = "La data di check-out è obbligatoria")
    @Future(message = "Il check-out deve essere una data futura")
    private LocalDate checkOutDate;
}
