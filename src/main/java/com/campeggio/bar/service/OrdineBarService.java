package com.campeggio.bar.service;

import com.campeggio.bar.dto.*;
import com.campeggio.bar.entity.*;
import com.campeggio.bar.repository.OrdineBarRepository;
import com.campeggio.exceptions.*;
import com.campeggio.users.entity.Ospite;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdineBarService {

    private final OrdineBarRepository ordineRepo;

    @Transactional
    public OrdineBarDTO create(CreateOrdineBarRequest req, Ospite ospite) {
        OrdineBar ordine = new OrdineBar();
        ordine.setOspite(ospite);
        ordine.setNumeroPiazzola(req.getNumeroPiazzola());
        ordine.setNote(req.getNote());

        BigDecimal totale = BigDecimal.ZERO;
        for (VoceOrdineRequest vReq : req.getVoci()) {
            VoceOrdine voce = new VoceOrdine();
            voce.setOrdine(ordine);
            voce.setNomeArticolo(vReq.getNomeArticolo());
            voce.setQuantita(vReq.getQuantita());
            voce.setPrezzoUnitario(vReq.getPrezzoUnitario());
            ordine.getVoci().add(voce);
            totale = totale.add(vReq.getPrezzoUnitario()
                    .multiply(BigDecimal.valueOf(vReq.getQuantita())));
        }
        ordine.setTotale(totale);

        return OrdineBarDTO.from(ordineRepo.save(ordine));
    }

    public Page<OrdineBarDTO> getAll(Pageable pageable) {
        return ordineRepo.findAll(pageable).map(OrdineBarDTO::from);
    }

    public Page<OrdineBarDTO> getByOspite(Long ospiteId, Pageable pageable) {
        return ordineRepo.findByOspiteId(ospiteId, pageable).map(OrdineBarDTO::from);
    }

    public OrdineBarDTO getById(Long id) {
        return ordineRepo.findById(id)
                .map(OrdineBarDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));
    }

    public List<OrdineBarDTO> getOrdiniAttivi() {
        return ordineRepo.findByStatoIn(List.of(StatoOrdine.IN_ATTESA, StatoOrdine.IN_PREPARAZIONE))
                .stream().map(OrdineBarDTO::from).toList();
    }

    @Transactional
    public OrdineBarDTO aggiornaStato(Long id, String nuovoStato) {
        OrdineBar ordine = ordineRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));

        StatoOrdine stato;
        try {
            stato = StatoOrdine.valueOf(nuovoStato.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Stato non valido: " + nuovoStato);
        }

        if (ordine.getStato() == StatoOrdine.ANNULLATO || ordine.getStato() == StatoOrdine.CONSEGNATO)
            throw new ConflictException("Impossibile modificare un ordine già " + ordine.getStato().name());

        ordine.setStato(stato);
        if (stato == StatoOrdine.CONSEGNATO) {
            ordine.setCompletatoAt(LocalDateTime.now());
        }

        return OrdineBarDTO.from(ordineRepo.save(ordine));
    }

    @Transactional
    public OrdineBarDTO annulla(Long id) {
        OrdineBar ordine = ordineRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));

        if (ordine.getStato() == StatoOrdine.CONSEGNATO)
            throw new ConflictException("Non è possibile annullare un ordine già consegnato");

        ordine.setStato(StatoOrdine.ANNULLATO);
        return OrdineBarDTO.from(ordineRepo.save(ordine));
    }
}
