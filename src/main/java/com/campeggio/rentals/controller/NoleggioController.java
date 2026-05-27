package com.campeggio.rentals.controller;

import com.campeggio.rentals.dto.*;
import com.campeggio.rentals.service.NoleggioService;
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
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class NoleggioController {

    private final NoleggioService service;

    @GetMapping("/items")
    public ResponseEntity<List<ArticoloNoleggioDTO>> getArticoliDisponibili() {
        return ResponseEntity.ok(service.getArticoliDisponibili());
    }

    @GetMapping
    public ResponseEntity<Page<NoleggioDTO>> getAll(
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
    public ResponseEntity<NoleggioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<NoleggioDTO> create(
            @AuthenticationPrincipal Ospite ospite,
            @Valid @RequestBody CreateNoleggioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, ospite));
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<NoleggioDTO> restituisci(@PathVariable Long id) {
        return ResponseEntity.ok(service.restituisci(id));
    }
}
