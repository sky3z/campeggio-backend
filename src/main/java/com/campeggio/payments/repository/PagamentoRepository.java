package com.campeggio.payments.repository;

import com.campeggio.payments.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByPrenotazioneId(Long prenotazioneId);

    Optional<Pagamento> findByStripePaymentIntentId(String paymentIntentId);

    List<Pagamento> findByStato(StatoPagamento stato);
}
