package com.campeggio.bar.dto;

import com.campeggio.bar.entity.OrdineBar;
import com.campeggio.bar.entity.VoceOrdine;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdineBarDTO {
    private Long id;
    private Long ospiteId;
    private String ospiteNome;
    private String numeroPiazzola;
    private List<VoceOrdineDTO> voci;
    private BigDecimal totale;
    private String stato;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completatoAt;

    @Data
    public static class VoceOrdineDTO {
        private Long id;
        private String nomeArticolo;
        private Integer quantita;
        private BigDecimal prezzoUnitario;
        private BigDecimal subtotale;

        public static VoceOrdineDTO from(VoceOrdine v) {
            VoceOrdineDTO dto = new VoceOrdineDTO();
            dto.setId(v.getId());
            dto.setNomeArticolo(v.getNomeArticolo());
            dto.setQuantita(v.getQuantita());
            dto.setPrezzoUnitario(v.getPrezzoUnitario());
            dto.setSubtotale(v.getPrezzoUnitario().multiply(BigDecimal.valueOf(v.getQuantita())));
            return dto;
        }
    }

    public static OrdineBarDTO from(OrdineBar o) {
        OrdineBarDTO dto = new OrdineBarDTO();
        dto.setId(o.getId());
        dto.setOspiteId(o.getOspite().getId());
        dto.setOspiteNome(o.getOspite().getName() + " " + o.getOspite().getSurname());
        dto.setNumeroPiazzola(o.getNumeroPiazzola());
        dto.setVoci(o.getVoci().stream().map(VoceOrdineDTO::from).toList());
        dto.setTotale(o.getTotale());
        dto.setStato(o.getStato().name());
        dto.setNote(o.getNote());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setCompletatoAt(o.getCompletatoAt());
        return dto;
    }
}
