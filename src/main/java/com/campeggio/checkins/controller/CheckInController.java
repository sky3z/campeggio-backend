package com.campeggio.checkins.controller;

import com.campeggio.checkins.dto.*;
import com.campeggio.checkins.service.CheckInService;
import com.campeggio.users.entity.Staff;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class CheckInController {

    private final CheckInService service;

    @GetMapping
    public ResponseEntity<Page<CheckInRecordDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @PostMapping("/in")
    public ResponseEntity<CheckInRecordDTO> checkIn(
            @AuthenticationPrincipal Staff staff,
            @Valid @RequestBody CheckInRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registraCheckIn(req, staff));
    }

    @PostMapping("/out")
    public ResponseEntity<CheckInRecordDTO> checkOut(
            @AuthenticationPrincipal Staff staff,
            @Valid @RequestBody CheckInRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registraCheckOut(req, staff));
    }
}
