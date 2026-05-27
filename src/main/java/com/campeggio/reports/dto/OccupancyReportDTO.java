package com.campeggio.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OccupancyReportDTO {
    private String startDate;
    private String endDate;
    private long activeReservations;
    private long totalAccommodations;
    private double occupancyRate;
}
