package com.api.rest.conveniencestore.reports.stock.controller;

import com.api.rest.conveniencestore.reports.stock.dto.StockReportDto;
import com.api.rest.conveniencestore.reports.stock.service.StockReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/stock")
@Tag(name = "Reports - Stock", description = "Stock report endpoints")
public class StockReportController {

    private final StockReportService stockReportService;

    public StockReportController(StockReportService stockReportService) {
        this.stockReportService = stockReportService;
    }

    @GetMapping
    @Operation(summary = "Generate stock summary report")
    public ResponseEntity<StockReportDto> getStockReport(
            @RequestParam(defaultValue = "7") int daysToExpire) {

        StockReportDto report = stockReportService.generateStockReport(daysToExpire);
        return ResponseEntity.ok(report);
    }
}
