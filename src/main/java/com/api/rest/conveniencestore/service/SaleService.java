package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.dto.SaleListingDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.*;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.repository.ClientRepository;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.repository.SaleRepository;
import com.api.rest.conveniencestore.utils.DateUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DateUtil dateUtil;

    @Transactional
    public Sale registerSale(SaleDto saleDto, Client client) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException, SaleNotValidPaymentMethodException {
        double totalValue = calculateTotalValue(saleDto);
        String description = generateSaleDescription(saleDto, client);
        int totalQuantity = saleDto.quantity().stream().mapToInt(Integer::intValue).sum();

        LocalDateTime saleDate = LocalDateTime.now();
        DateUtil.formatDate(saleDate);

        Sale sale = new Sale(saleDto, totalValue, description, totalQuantity, saleDate);

        validatePaymentMethod(sale, sale.getPaymentMethod());

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);

            Product product;
            product = validationProduct(productId, quantity);

            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }
        return saleRepository.save(sale);
    }


    public List<SaleListingDto> listSalesByPaymentMethod(PaymentMethod payment) {
        return saleRepository.findByPaymentMethod(payment)
                .stream()
                .map(SaleListingDto::new)
                .collect(Collectors.toList());
    }

    private double calculateTotalValue(SaleDto saleDto) throws ProductNotFoundException {
        double total = 0;
        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
            total += product.getPrice() * quantity;
        }
        return total;
    }


    private String generateSaleDescription(SaleDto saleDto, Client client) throws ProductNotFoundException {
        StringBuilder description = new StringBuilder();


        description.append("CPF: ").append(client.getCpf()).append("Products: ");

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
            description.append("cod")
                    .append(saleDto.productIds().get(i))
                    .append(" ")
                    .append(product.getName())
                    .append(" ")
                    .append(saleDto.quantity())
                    .append("x - R$")
                    .append(String.format("%.2f ", product.getPrice()))
                    .append(" ");
        }
        return description.toString();
    }

    private Product validationProduct(Long productId, int quantity) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found:: " + productId));

        if (product.getStatus().equals(Status.INACTIVE)) {
            throw new ProductInactiveException("Inactive product: " + product.getName());
        }

        if (product.getStockQuantity() < quantity) {
            throw new ProductInsufficientStockException("Insufficient stock for the product: " + product.getName() + " " + product.getStockQuantity() + ", unidades em estoque.");
        }
        return product;
    }

    private void validatePaymentMethod(Sale sale, PaymentMethod paymentMethod) throws SaleNotValidPaymentMethodException {
        if (paymentMethod == null) {
            throw new SaleNotValidPaymentMethodException("Payment method cannot be empty");
        }

        if (!sale.getPaymentMethod().equals(paymentMethod)) {
            throw new SaleNotValidPaymentMethodException("Invalid payment method: " + paymentMethod);
        }
    }
}



