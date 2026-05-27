package com.campeggio.rentals.dto;

import com.campeggio.rentals.entity.ArticoloNoleggio;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticoloNoleggioDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private BigDecimal prezzoGiornaliero;
    private Integer quantitaDisponibile;

    public static ArticoloNoleggioDTO from(ArticoloNoleggio a) {
        ArticoloNoleggioDTO dto = new ArticoloNoleggioDTO();
        dto.setId(a.getId());
        dto.setNome(a.getNome());
        dto.setDescrizione(a.getDescrizione());
        dto.setPrezzoGiornaliero(a.getPrezzoGiornaliero());
        dto.setQuantitaDisponibile(a.getQuantitaDisponibile());
        return dto;
    }
}
