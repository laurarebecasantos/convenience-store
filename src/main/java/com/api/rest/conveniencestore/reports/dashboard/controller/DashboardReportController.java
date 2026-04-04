package com.api.rest.conveniencestore.reports.dashboard.controller;

import com.api.rest.conveniencestore.reports.dashboard.dto.DashboardReportDto;
import com.api.rest.conveniencestore.reports.dashboard.service.DashboardReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/dashboard")
@Tag(name = "Reports - Dashboard", description = "Dashboard summary endpoint")
public class DashboardReportController {

    private final DashboardReportService dashboardReportService;

    public DashboardReportController(DashboardReportService dashboardReportService) {
        this.dashboardReportService = dashboardReportService;
    }

    @GetMapping
    @Operation(summary = "Generate daily dashboard summary")
    public ResponseEntity<DashboardReportDto> getDashboard() {
        DashboardReportDto dashboard = dashboardReportService.generateDashboard();
        return ResponseEntity.ok(dashboard);
    }
}
