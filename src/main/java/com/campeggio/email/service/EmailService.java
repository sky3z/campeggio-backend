package com.campeggio.email.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Value("${sendgrid.api.key:}")
    private String apiKey;

    @Value("${sendgrid.from.email:noreply@campeggio.it}")
    private String fromEmail;

    /**
     * Invia un'email generica. Se la chiave non è configurata, logga solo.
     */
    public void sendEmail(String toEmail, String toName, String subject, String htmlContent) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("[EMAIL SIMULATA] A: {} <{}> | Oggetto: {}", toName, toEmail, subject);
            log.debug("[EMAIL SIMULATA] Contenuto:\n{}", htmlContent);
            return;
        }

        Email from = new Email(fromEmail, "Campeggio");
        Email to = new Email(toEmail, toName);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 400) {
                log.error("Errore SendGrid: status={}, body={}", response.getStatusCode(), response.getBody());
            } else {
                log.info("Email inviata a {} — status {}", toEmail, response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Errore durante l'invio dell'email a {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendBookingConfirmation(String email, String name, Long prenotazioneId,
                                        String accommodation, String checkIn, String checkOut) {
        String subject = "Conferma prenotazione #" + prenotazioneId;
        String html = """
                <h2>Prenotazione confermata!</h2>
                <p>Caro/a <strong>%s</strong>,</p>
                <p>La tua prenotazione è stata confermata con successo.</p>
                <ul>
                  <li><strong>Alloggio:</strong> %s</li>
                  <li><strong>Check-in:</strong> %s</li>
                  <li><strong>Check-out:</strong> %s</li>
                  <li><strong>N° prenotazione:</strong> %d</li>
                </ul>
                <p>Ti aspettiamo!</p>
                <p><em>Il team del Campeggio</em></p>
                """.formatted(name, accommodation, checkIn, checkOut, prenotazioneId);
        sendEmail(email, name, subject, html);
    }

    public void sendCheckInReminder(String email, String name, String accommodation, String checkIn) {
        String subject = "Promemoria check-in domani — " + accommodation;
        String html = """
                <h2>Ci vediamo domani!</h2>
                <p>Caro/a <strong>%s</strong>,</p>
                <p>Ti ricordiamo che domani è il tuo check-in:</p>
                <ul>
                  <li><strong>Alloggio:</strong> %s</li>
                  <li><strong>Data check-in:</strong> %s</li>
                </ul>
                <p>Il check-in è disponibile dalle ore 14:00. Porta un documento d'identità valido.</p>
                <p><em>Il team del Campeggio</em></p>
                """.formatted(name, accommodation, checkIn);
        sendEmail(email, name, subject, html);
    }

    public void sendContractExpiryWarning(String email, String name, String accommodation, String expiryDate) {
        String subject = "Scadenza contratto piazzola — " + accommodation;
        String html = """
                <h2>Il tuo contratto sta per scadere</h2>
                <p>Caro/a <strong>%s</strong>,</p>
                <p>Ti informiamo che il contratto per la tua piazzola fissa sta per scadere:</p>
                <ul>
                  <li><strong>Piazzola:</strong> %s</li>
                  <li><strong>Scadenza contratto:</strong> %s</li>
                </ul>
                <p>Contattaci per rinnovare il contratto.</p>
                <p><em>Il team del Campeggio</em></p>
                """.formatted(name, accommodation, expiryDate);
        sendEmail(email, name, subject, html);
    }
}
