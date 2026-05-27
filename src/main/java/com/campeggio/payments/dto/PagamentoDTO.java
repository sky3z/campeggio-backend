package com.campeggio.payments.dto;

import com.campeggio.payments.entity.Pagamento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoDTO {
    private Long id;
    private Long prenotazioneId;
    private String stripePaymentIntentId;
    private BigDecimal amount;
    private String currency;
    private String stato;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public static PagamentoDTO from(Pagamento p) {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setId(p.getId());
        dto.setPrenotazioneId(p.getPrenotazione().getId());
        dto.setStripePaymentIntentId(p.getStripePaymentIntentId());
        dto.setAmount(p.getAmount());
        dto.setCurrency(p.getCurrency());
        dto.setStato(p.getStato().name());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setPaidAt(p.getPaidAt());
        return dto;
    }
}
