package com.campeggio.accommodations.controller;

import com.campeggio.accommodations.dto.*;
import com.campeggio.accommodations.entity.AccommodationStatus;
import com.campeggio.accommodations.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campeggio.common.PagedResponse;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService service;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("pricePerNight", "name", "maxCapacity", "status");

    @GetMapping
    public ResponseEntity<PagedResponse<AccommodationDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "pricePerNight") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) AccommodationStatus status) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "pricePerNight";
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PagedResponse.from(service.getAll(pageable, type, status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AccommodationDTO>> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int capacity,
            @RequestParam(defaultValue = "PIAZZOLA") String type) {
        return ResponseEntity.ok(service.findAvailable(checkIn, checkOut, capacity, type));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> create(@Valid @RequestBody CreateAccommodationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody CreateAccommodationRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<AccommodationDTO> updateStatus(@PathVariable Long id,
                                                          @RequestParam AccommodationStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
