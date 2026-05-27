package com.campeggio.rentals.service;

import com.campeggio.exceptions.*;
import com.campeggio.rentals.dto.*;
import com.campeggio.rentals.entity.*;
import com.campeggio.rentals.repository.*;
import com.campeggio.users.entity.Ospite;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoleggioService {

    private final NoleggioRepository noleggioRepo;
    private final ArticoloNoleggioRepository articoloRepo;

    public List<ArticoloNoleggioDTO> getArticoliDisponibili() {
        return articoloRepo.findByAttivoTrue().stream()
                .map(ArticoloNoleggioDTO::from)
                .toList();
    }

    @Transactional
    public NoleggioDTO create(CreateNoleggioRequest req, Ospite ospite) {
        if (!req.getDataFine().isAfter(req.getDataInizio()))
            throw new IllegalArgumentException("La data di fine deve essere successiva alla data di inizio");

        ArticoloNoleggio articolo = articoloRepo.findById(req.getArticoloId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Articolo non trovato con id: " + req.getArticoloId()));

        if (!articolo.isAttivo())
            throw new ConflictException("L'articolo non è disponibile al noleggio");

        if (articolo.getQuantitaDisponibile() < req.getQuantita())
            throw new ConflictException("Quantità richiesta non disponibile. Disponibili: "
                    + articolo.getQuantitaDisponibile());

        Noleggio noleggio = new Noleggio();
        noleggio.setOspite(ospite);
        noleggio.setArticolo(articolo);
        noleggio.setDataInizio(req.getDataInizio());
        noleggio.setDataFine(req.getDataFine());
        noleggio.setQuantita(req.getQuantita());

        // Aggiorna disponibilità
        articolo.setQuantitaDisponibile(articolo.getQuantitaDisponibile() - req.getQuantita());
        articoloRepo.save(articolo);

        return NoleggioDTO.from(noleggioRepo.save(noleggio));
    }

    public Page<NoleggioDTO> getAll(Pageable pageable) {
        return noleggioRepo.findAll(pageable).map(NoleggioDTO::from);
    }

    public Page<NoleggioDTO> getByOspite(Long ospiteId, Pageable pageable) {
        return noleggioRepo.findByOspiteId(ospiteId, pageable).map(NoleggioDTO::from);
    }

    public NoleggioDTO getById(Long id) {
        return noleggioRepo.findById(id)
                .map(NoleggioDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato con id: " + id));
    }

    @Transactional
    public NoleggioDTO restituisci(Long id) {
        Noleggio noleggio = noleggioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato con id: " + id));

        if (noleggio.getStato() != StatoNoleggio.ATTIVO)
            throw new ConflictException("Solo i noleggi ATTIVI possono essere restituiti");

        noleggio.setStato(StatoNoleggio.RESTITUITO);

        // Ripristina disponibilità
        ArticoloNoleggio articolo = noleggio.getArticolo();
        articolo.setQuantitaDisponibile(articolo.getQuantitaDisponibile() + noleggio.getQuantita());
        articoloRepo.save(articolo);

        return NoleggioDTO.from(noleggioRepo.save(noleggio));
    }
}
