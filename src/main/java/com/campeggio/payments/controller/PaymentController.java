package com.campeggio.payments.controller;

import com.campeggio.payments.dto.*;
import com.campeggio.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    /**
     * Crea un PaymentIntent Stripe per una prenotazione confermata.
     * Il client riceve il clientSecret per completare il pagamento con Stripe.js
     */
    @PostMapping("/intent/{prenotazioneId}")
    @PreAuthorize("hasAnyRole('OSPITE','ADMIN','STAFF')")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @PathVariable Long prenotazioneId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPaymentIntent(prenotazioneId));
    }

    /**
     * Webhook Stripe — riceve notifiche di pagamento (no auth, verifica con firma HMAC).
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        service.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    /**
     * Restituisce i dettagli del pagamento associato a una prenotazione.
     */
    @GetMapping("/prenotazione/{prenotazioneId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','OSPITE')")
    public ResponseEntity<PagamentoDTO> getByPrenotazione(@PathVariable Long prenotazioneId) {
        return ResponseEntity.ok(service.getByPrenotazione(prenotazioneId));
    }
}
