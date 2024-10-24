package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.ProductInactiveException;
import com.api.rest.conveniencestore.exceptions.ProductInsufficientStockException;
import com.api.rest.conveniencestore.exceptions.ProductNotFoundException;
import com.api.rest.conveniencestore.exceptions.SaleNotValidPaymentMethodException;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.utils.MessageConstants;

public class SaleHelper {

    private final ProductRepository productRepository;

    public SaleHelper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public double calculateTotalValue(SaleDto saleDto) throws ProductNotFoundException {
        double total = 0;
        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, productId)));
            total += product.getPrice() * quantity;
        }
        return total;
    }

    public String generateSaleDescription(SaleDto saleDto, Client client) throws ProductNotFoundException {
        StringBuilder description = new StringBuilder();


        description.append("CPF: ").append(client.getCpf()).append(" Produtos: ");

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, productId)));
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

    public Product validationProduct(Long productId, int quantity) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, productId));
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, productId)));

        if (product.getStatus().equals(Status.INACTIVE)) {
            throw new ProductInactiveException(MessageConstants.INVALID_PRODUCT + product.getName());
        }

        if (product.getStockQuantity() < quantity) {
            throw new ProductInsufficientStockException("Estoque insuficiente para o produto: " + product.getName() + " " + product.getStockQuantity() + ", unidades em estoque.");
        }
        return product;
    }

    public void validatePaymentMethod(Sale sale, PaymentMethod paymentMethod) throws SaleNotValidPaymentMethodException {
        if (paymentMethod == null) {
            throw new SaleNotValidPaymentMethodException(MessageConstants.PAYMENT_METHOD_EMPTY);
        }

        if (!sale.getPaymentMethod().equals(paymentMethod)) {
            throw new SaleNotValidPaymentMethodException(MessageConstants.INVALID_PAYMENT_METHOD + paymentMethod);
        }
    }
}