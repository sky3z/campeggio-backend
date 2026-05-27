package com.campeggio.bar.controller;

import com.campeggio.bar.dto.*;
import com.campeggio.bar.service.OrdineBarService;
import com.campeggio.users.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bar/orders")
@RequiredArgsConstructor
public class OrdineBarController {

    private final OrdineBarService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Page<OrdineBarDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<Page<OrdineBarDTO>> getMine(
            @AuthenticationPrincipal Ospite ospite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.getByOspite(ospite.getId(), pageable));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<OrdineBarDTO>> getOrdiniAttivi() {
        return ResponseEntity.ok(service.getOrdiniAttivi());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdineBarDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<OrdineBarDTO> create(
            @AuthenticationPrincipal Ospite ospite,
            @Valid @RequestBody CreateOrdineBarRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, ospite));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<OrdineBarDTO> aggiornaStato(
            @PathVariable Long id,
            @RequestParam String stato) {
        return ResponseEntity.ok(service.aggiornaStato(id, stato));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrdineBarDTO> annulla(@PathVariable Long id) {
        return ResponseEntity.ok(service.annulla(id));
    }
}
