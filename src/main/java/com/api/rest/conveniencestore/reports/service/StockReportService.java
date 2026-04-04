package com.api.rest.conveniencestore.reports.service;

import com.api.rest.conveniencestore.reports.dto.StockReportDto;
import com.api.rest.conveniencestore.reports.repository.StockReportRepository;
import org.springframework.stereotype.Service;

@Service
public class StockReportService {

    private final StockReportRepository stockReportRepository;

    public StockReportService(StockReportRepository stockReportRepository) {
        this.stockReportRepository = stockReportRepository;
    }

    public StockReportDto generateStockReport(int daysToExpire) {
        return new StockReportDto(stockReportRepository.findStockSummary(daysToExpire));
    }
}
