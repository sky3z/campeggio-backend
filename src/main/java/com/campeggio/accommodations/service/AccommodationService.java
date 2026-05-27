package com.campeggio.accommodations.service;

import com.campeggio.accommodations.dto.*;
import com.campeggio.accommodations.entity.*;
import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.exceptions.ResourceNotFoundException;
import com.campeggio.users.entity.Ospite;
import com.campeggio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository repository;
    private final UserRepository userRepository;

    public Page<AccommodationDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(AccommodationDTO::from);
    }

    public AccommodationDTO getById(Long id) {
        return repository.findById(id)
                .map(AccommodationDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato con id: " + id));
    }

    public List<AccommodationDTO> findAvailable(LocalDate checkIn, LocalDate checkOut,
                                                 int capacity, String type) {
        if (!checkOut.isAfter(checkIn))
            throw new IllegalArgumentException("La data di check-out deve essere successiva al check-in");
        return repository.findAvailable(checkIn, checkOut, capacity, type)
                .stream().map(AccommodationDTO::from).toList();
    }

    public AccommodationDTO create(CreateAccommodationRequest req) {
        Accommodation acc = buildFromRequest(req, null);
        return AccommodationDTO.from(repository.save(acc));
    }

    public AccommodationDTO update(Long id, CreateAccommodationRequest req) {
        Accommodation existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato con id: " + id));
        Accommodation updated = buildFromRequest(req, existing);
        return AccommodationDTO.from(repository.save(updated));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Alloggio non trovato con id: " + id);
        repository.deleteById(id);
    }

    public AccommodationDTO updateStatus(Long id, AccommodationStatus status) {
        Accommodation acc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato con id: " + id));
        acc.setStatus(status);
        return AccommodationDTO.from(repository.save(acc));
    }

    private Accommodation buildFromRequest(CreateAccommodationRequest req, Accommodation existing) {
        Accommodation acc = switch (req.getType()) {
            case "PIAZZOLA" -> {
                Piazzola p = existing instanceof Piazzola ep ? ep : new Piazzola();
                if (req.getTipoPiazzola() != null)
                    p.setTipoPiazzola(Piazzola.TipoPiazzola.valueOf(req.getTipoPiazzola()));
                p.setSurfaceM2(req.getSurfaceM2());
                p.setHasElectricity(Boolean.TRUE.equals(req.getHasElectricity()));
                p.setHasWater(Boolean.TRUE.equals(req.getHasWater()));
                yield p;
            }
            case "BUNGALOW" -> {
                Bungalow b = existing instanceof Bungalow eb ? eb : new Bungalow();
                if (req.getRooms() != null) b.setRooms(req.getRooms());
                if (req.getBeds() != null) b.setBeds(req.getBeds());
                b.setHasBathroom(Boolean.TRUE.equals(req.getHasBathroom()));
                b.setHasKitchen(Boolean.TRUE.equals(req.getHasKitchen()));
                yield b;
            }
            case "PIAZZOLA_FISSA" -> {
                PiazzolaFissa pf = existing instanceof PiazzolaFissa epf ? epf : new PiazzolaFissa();
                pf.setAnnualFee(req.getAnnualFee());
                pf.setHasPrivateEntrance(Boolean.TRUE.equals(req.getHasPrivateEntrance()));
                if (req.getContractStart() != null)
                    pf.setContractStart(LocalDate.parse(req.getContractStart()));
                if (req.getContractEnd() != null)
                    pf.setContractEnd(LocalDate.parse(req.getContractEnd()));
                if (req.getOwnerId() != null) {
                    Ospite owner = (Ospite) userRepository.findById(req.getOwnerId())
                            .orElseThrow(() -> new ResourceNotFoundException("Ospite non trovato con id: " + req.getOwnerId()));
                    pf.setOwner(owner);
                }
                yield pf;
            }
            default -> throw new IllegalArgumentException("Tipo non valido: " + req.getType());
        };

        acc.setName(req.getName());
        acc.setDescription(req.getDescription());
        acc.setPricePerNight(req.getPricePerNight());
        acc.setMaxCapacity(req.getMaxCapacity());
        return acc;
    }
}
