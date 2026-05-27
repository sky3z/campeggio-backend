package com.campeggio.rentals.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateNoleggioRequest {

    @NotNull(message = "L'ID articolo è obbligatorio")
    private Long articoloId;

    @NotNull(message = "La data di inizio è obbligatoria")
    @FutureOrPresent(message = "La data di inizio non può essere nel passato")
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine è obbligatoria")
    @Future(message = "La data di fine deve essere futura")
    private LocalDate dataFine;

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantita;
}
