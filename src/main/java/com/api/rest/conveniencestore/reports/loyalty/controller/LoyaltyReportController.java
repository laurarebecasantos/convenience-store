package com.api.rest.conveniencestore.reports.loyalty.controller;

import com.api.rest.conveniencestore.reports.loyalty.dto.LoyaltyReportDto;
import com.api.rest.conveniencestore.reports.loyalty.service.LoyaltyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/loyalty")
@Tag(name = "Reports - Loyalty", description = "Loyalty program report endpoints")
public class LoyaltyReportController {

    private final LoyaltyReportService loyaltyReportService;

    public LoyaltyReportController(LoyaltyReportService loyaltyReportService) {
        this.loyaltyReportService = loyaltyReportService;
    }

    @GetMapping
    @Operation(summary = "Generate loyalty program report")
    public ResponseEntity<LoyaltyReportDto> getLoyaltyReport() {
        LoyaltyReportDto report = loyaltyReportService.generateLoyaltyReport();
        return ResponseEntity.ok(report);
    }
}
