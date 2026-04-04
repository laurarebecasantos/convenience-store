package com.api.rest.conveniencestore.reports.controller;

import com.api.rest.conveniencestore.reports.dto.SalesReportDto;
import com.api.rest.conveniencestore.reports.service.SalesReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports/sales")
@Tag(name = "Reports - Sales", description = "Sales report endpoints")
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping
    @Operation(summary = "Generate sales report grouped by period")
    public ResponseEntity<SalesReportDto> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAY") String groupBy) {

        SalesReportDto report = salesReportService.generateSalesReport(startDate, endDate, groupBy);
        return ResponseEntity.ok(report);
    }
}
