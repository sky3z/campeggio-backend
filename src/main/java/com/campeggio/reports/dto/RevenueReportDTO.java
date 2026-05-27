package com.campeggio.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueReportDTO {
    private String mese;
    private BigDecimal totale;
}
