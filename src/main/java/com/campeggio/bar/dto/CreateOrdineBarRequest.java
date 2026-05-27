package com.campeggio.bar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrdineBarRequest {

    private String numeroPiazzola;

    @NotEmpty(message = "L'ordine deve contenere almeno un articolo")
    @Valid
    private List<VoceOrdineRequest> voci;

    private String note;
}
