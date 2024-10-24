package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.dto.SaleListingDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.*;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.repository.SaleRepository;
import com.api.rest.conveniencestore.service.SaleHelper;
import com.api.rest.conveniencestore.service.SaleService;
import com.api.rest.conveniencestore.utils.MessageConstants;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Sale> register(@Valid @RequestBody SaleDto saleDto, Client client, SaleHelper saleHelper) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException, SaleNotValidPaymentMethodException, CpfValidateException {
        Sale savedSale = saleService.registerSale(saleDto, client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
    }

    @GetMapping
    public ResponseEntity<List<SaleListingDto>> listSalesByPaymentMethod(@Valid @RequestParam String paymentMethod) throws SaleListingNullException {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new SaleListingNullException(MessageConstants.PAYMENT_METHOD_EMPTY);
        }
        PaymentMethod payment;
        try {
            payment = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SaleListingNullException(MessageConstants.INVALID_PAYMENT_METHOD + paymentMethod);
        }
        List<SaleListingDto> sales = saleService.listSalesByPaymentMethod(payment);
        return ResponseEntity.ok(sales);
    }


    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<Sale> status(@Valid @PathVariable Long id, @RequestBody Map<String, String> statusRequest) throws SaleInvalidStatusException, SaleListingNullException {
        String statusString = statusRequest.get("status");
        Status statusCanceled;
        try {
            statusCanceled = Status.fromValueStatus(statusString);
        } catch (IllegalArgumentException e) {
            throw new SaleInvalidStatusException(MessageConstants.INVALID_STATUS + statusString);
        }

        if (!Status.CANCELLED.equals(statusCanceled)) {
            throw new SaleInvalidStatusException(MessageConstants.STATUS_CANCELLED);
        }

        if (!saleService.existsById(id)) {
            throw new SaleListingNullException(MessageConstants.SALE_NOT_FOUND);
        }

        Sale updatedStatusSale = saleService.statusSaleCanceled(id, statusCanceled);
        return ResponseEntity.ok(updatedStatusSale);
    }
}
