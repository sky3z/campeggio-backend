package com.campeggio.reports.controller;

import com.campeggio.reports.dto.*;
import com.campeggio.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService service;

    /**
     * Rapporto occupazione in un periodo.
     * Es: GET /api/admin/reports/occupancy?startDate=2025-06-01&endDate=2025-06-30
     */
    @GetMapping("/occupancy")
    public ResponseEntity<OccupancyReportDTO> getOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(service.getOccupancy(startDate, endDate));
    }

    /**
     * Fatturato per mese in un intervallo di date.
     * Es: GET /api/admin/reports/revenue?start=2025-01-01&end=2025-12-31
     */
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReportDTO>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.getRevenueByMonth(start, end));
    }
}
