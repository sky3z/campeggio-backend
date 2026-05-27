package com.campeggio.reservations.controller;

import com.campeggio.reservations.dto.*;
import com.campeggio.reservations.service.PrenotazioneService;
import com.campeggio.users.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class PrenotazioneController {

    private final PrenotazioneService service;

    @GetMapping
    public ResponseEntity<Page<PrenotazioneDTO>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (user.getRole() == Role.OSPITE) {
            return ResponseEntity.ok(service.getByOspite(user.getId(), pageable));
        }
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrenotazioneDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<PrenotazioneDTO> create(
            @AuthenticationPrincipal Ospite ospite,
            @Valid @RequestBody CreatePrenotazioneRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, ospite));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<PrenotazioneDTO> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirm(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PrenotazioneDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }
}
