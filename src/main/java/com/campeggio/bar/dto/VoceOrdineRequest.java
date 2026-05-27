package com.campeggio.bar.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoceOrdineRequest {

    @NotBlank(message = "Il nome articolo è obbligatorio")
    private String nomeArticolo;

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantita;

    @NotNull(message = "Il prezzo unitario è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere positivo")
    private BigDecimal prezzoUnitario;
}
