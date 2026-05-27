package com.campeggio.rentals.dto;

import com.campeggio.rentals.entity.Noleggio;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NoleggioDTO {
    private Long id;
    private Long ospiteId;
    private String ospiteNome;
    private Long articoloId;
    private String articoloNome;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private Integer quantita;
    private BigDecimal totalPrice;
    private String stato;
    private LocalDateTime createdAt;

    public static NoleggioDTO from(Noleggio n) {
        NoleggioDTO dto = new NoleggioDTO();
        dto.setId(n.getId());
        dto.setOspiteId(n.getOspite().getId());
        dto.setOspiteNome(n.getOspite().getName() + " " + n.getOspite().getSurname());
        dto.setArticoloId(n.getArticolo().getId());
        dto.setArticoloNome(n.getArticolo().getNome());
        dto.setDataInizio(n.getDataInizio());
        dto.setDataFine(n.getDataFine());
        dto.setQuantita(n.getQuantita());
        dto.setTotalPrice(n.getTotalPrice());
        dto.setStato(n.getStato().name());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
