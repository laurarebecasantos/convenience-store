package com.api.rest.conveniencestore.reports.sales.service;

import com.api.rest.conveniencestore.reports.sales.dto.SalesPeriodReportDto;
import com.api.rest.conveniencestore.reports.sales.dto.SalesReportDto;
import com.api.rest.conveniencestore.reports.sales.projection.SalesPeriodProjection;
import com.api.rest.conveniencestore.reports.sales.repository.SalesReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SalesReportService {

    private final SalesReportRepository salesReportRepository;

    public SalesReportService(SalesReportRepository salesReportRepository) {
        this.salesReportRepository = salesReportRepository;
    }

    public SalesReportDto generateSalesReport(LocalDate startDate, LocalDate endDate, String groupBy) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<SalesPeriodProjection> projections = switch (groupBy.toUpperCase()) {
            case "WEEK" -> salesReportRepository.findSalesGroupedByWeek(startDateTime, endDateTime);
            case "MONTH" -> salesReportRepository.findSalesGroupedByMonth(startDateTime, endDateTime);
            default -> salesReportRepository.findSalesGroupedByDay(startDateTime, endDateTime);
        };

        List<SalesPeriodReportDto> periods = projections.stream()
                .map(SalesPeriodReportDto::new)
                .toList();

        return new SalesReportDto(startDate, endDate, groupBy.toUpperCase(), periods);
    }
}
