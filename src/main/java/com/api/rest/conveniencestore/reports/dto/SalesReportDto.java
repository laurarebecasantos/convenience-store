package com.api.rest.conveniencestore.reports.dto;

import java.time.LocalDate;
import java.util.List;

public record SalesReportDto(
        LocalDate startDate,
        LocalDate endDate,
        String groupBy,
        List<SalesPeriodReportDto> periods
) {
}
