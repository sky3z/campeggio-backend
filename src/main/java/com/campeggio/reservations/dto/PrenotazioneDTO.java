package com.campeggio.reservations.dto;

import com.campeggio.reservations.entity.Prenotazione;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PrenotazioneDTO {
    private Long id;
    private Long ospiteId;
    private String ospiteNome;
    private Long accommodationId;
    private String accommodationName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String stato;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public static PrenotazioneDTO from(Prenotazione p) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.setId(p.getId());
        dto.setOspiteId(p.getOspite().getId());
        dto.setOspiteNome(p.getOspite().getName() + " " + p.getOspite().getSurname());
        dto.setAccommodationId(p.getAccommodation().getId());
        dto.setAccommodationName(p.getAccommodation().getName());
        dto.setCheckInDate(p.getCheckInDate());
        dto.setCheckOutDate(p.getCheckOutDate());
        dto.setStato(p.getStato().name());
        dto.setTotalPrice(p.getTotalPrice());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
