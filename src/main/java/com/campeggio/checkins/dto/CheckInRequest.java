package com.campeggio.checkins.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {

    @NotNull(message = "L'ID prenotazione è obbligatorio")
    private Long prenotazioneId;

    private String note;
}
