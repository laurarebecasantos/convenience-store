package com.api.rest.conveniencestore.loyalty.controller;

import com.api.rest.conveniencestore.loyalty.dto.LoyaltySimulateDto;
import com.api.rest.conveniencestore.loyalty.dto.LoyaltySimulateResponseDto;
import com.api.rest.conveniencestore.loyalty.dto.LoyaltyTransactionDto;
import com.api.rest.conveniencestore.client.model.Client;
import com.api.rest.conveniencestore.client.service.ClientService;
import com.api.rest.conveniencestore.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/simulate")
    public ResponseEntity<LoyaltySimulateResponseDto> simulate(@Valid @RequestBody LoyaltySimulateDto dto) {
        Client client = clientService.findById(dto.clientId());
        LoyaltySimulateResponseDto response = loyaltyService.simulate(client, dto.purchaseAmount(), dto.pointsToUse());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clients/{id}/transactions")
    public ResponseEntity<Page<LoyaltyTransactionDto>> getTransactions(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(loyaltyService.getTransactions(id, pageable));
    }
}
