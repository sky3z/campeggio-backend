package com.campeggio.payments.service;

import com.campeggio.exceptions.*;
import com.campeggio.payments.dto.*;
import com.campeggio.payments.entity.*;
import com.campeggio.payments.repository.PagamentoRepository;
import com.campeggio.reservations.entity.*;
import com.campeggio.reservations.repository.PrenotazioneRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PagamentoRepository pagamentoRepo;
    private final PrenotazioneRepository prenotazioneRepo;

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (stripeApiKey != null && !stripeApiKey.isBlank()) {
            Stripe.apiKey = stripeApiKey;
        }
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(Long prenotazioneId) {
        Prenotazione prenotazione = prenotazioneRepo.findById(prenotazioneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prenotazione non trovata con id: " + prenotazioneId));

        if (prenotazione.getStato() != StatoPrenotazione.CONFERMATA)
            throw new ConflictException("La prenotazione deve essere CONFERMATA per procedere al pagamento");

        if (pagamentoRepo.findByPrenotazioneId(prenotazioneId).isPresent())
            throw new ConflictException("Esiste già un pagamento per questa prenotazione");

        // Importo in centesimi per Stripe
        BigDecimal amount = prenotazione.getTotalPrice();
        long amountCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency("eur")
                    .putMetadata("prenotazione_id", String.valueOf(prenotazioneId))
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Pagamento pagamento = new Pagamento();
            pagamento.setPrenotazione(prenotazione);
            pagamento.setStripePaymentIntentId(intent.getId());
            pagamento.setAmount(amount);
            pagamento.setCurrency("eur");
            pagamentoRepo.save(pagamento);

            return new PaymentIntentResponse(
                    intent.getClientSecret(),
                    intent.getId(),
                    amount,
                    "eur");

        } catch (StripeException e) {
            log.error("Errore Stripe durante la creazione del PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Errore durante l'inizializzazione del pagamento: " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("Webhook secret non configurato — evento ignorato");
            return;
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new UnauthorizedException("Firma webhook Stripe non valida");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (intent != null) {
                    pagamentoRepo.findByStripePaymentIntentId(intent.getId())
                            .ifPresent(p -> {
                                p.setStato(StatoPagamento.COMPLETATO);
                                p.setPaidAt(LocalDateTime.now());
                                pagamentoRepo.save(p);
                                log.info("Pagamento {} completato", p.getId());
                            });
                }
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (intent != null) {
                    pagamentoRepo.findByStripePaymentIntentId(intent.getId())
                            .ifPresent(p -> {
                                p.setStato(StatoPagamento.FALLITO);
                                pagamentoRepo.save(p);
                                log.warn("Pagamento {} fallito", p.getId());
                            });
                }
            }
            default -> log.debug("Evento Stripe non gestito: {}", event.getType());
        }
    }

    public PagamentoDTO getByPrenotazione(Long prenotazioneId) {
        return pagamentoRepo.findByPrenotazioneId(prenotazioneId)
                .map(PagamentoDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pagamento non trovato per prenotazione id: " + prenotazioneId));
    }
}
