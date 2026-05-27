package com.campeggio.checkins.dto;

import com.campeggio.checkins.entity.CheckInRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckInRecordDTO {
    private Long id;
    private Long prenotazioneId;
    private String ospiteNome;
    private String accommodationName;
    private String registratoDa;
    private String tipo;
    private String note;
    private LocalDateTime timestamp;

    public static CheckInRecordDTO from(CheckInRecord r) {
        CheckInRecordDTO dto = new CheckInRecordDTO();
        dto.setId(r.getId());
        dto.setPrenotazioneId(r.getPrenotazione().getId());
        dto.setOspiteNome(r.getPrenotazione().getOspite().getName()
                + " " + r.getPrenotazione().getOspite().getSurname());
        dto.setAccommodationName(r.getPrenotazione().getAccommodation().getName());
        if (r.getRegistratoDa() != null) {
            dto.setRegistratoDa(r.getRegistratoDa().getName() + " " + r.getRegistratoDa().getSurname());
        }
        dto.setTipo(r.getTipo().name());
        dto.setNote(r.getNote());
        dto.setTimestamp(r.getTimestamp());
        return dto;
    }
}
