package com.campeggio.reports.service;

import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.reports.dto.*;
import com.campeggio.reservations.repository.PrenotazioneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PrenotazioneRepository prenotazioneRepo;
    private final AccommodationRepository accommodationRepo;

    public OccupancyReportDTO getOccupancy(LocalDate startDate, LocalDate endDate) {
        long active = prenotazioneRepo.countActiveInPeriod(startDate, endDate);
        long total = accommodationRepo.count();
        double rate = total > 0 ? (double) active / total * 100 : 0;
        return new OccupancyReportDTO(
                startDate.toString(),
                endDate.toString(),
                active,
                total,
                Math.round(rate * 10.0) / 10.0);
    }

    public List<RevenueReportDTO> getRevenueByMonth(LocalDate start, LocalDate end) {
        List<Object[]> rows = prenotazioneRepo.revenueByMonth(start, end);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        return rows.stream().map(row -> {
            // row[0] = Timestamp (PostgreSQL DATE_TRUNC), row[1] = BigDecimal
            String mese = row[0].toString().substring(0, 7); // "yyyy-MM-dd..." → "yyyy-MM"
            BigDecimal totale = (BigDecimal) row[1];
            return new RevenueReportDTO(mese, totale);
        }).toList();
    }
}
