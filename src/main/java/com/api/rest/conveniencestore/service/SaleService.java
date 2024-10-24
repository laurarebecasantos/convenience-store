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

    private SaleHelper saleHelper;

    @Autowired
    public void setSaleHelper() {
        this.saleHelper = new SaleHelper(productRepository);
    }

    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }

    @Transactional
    public Sale registerSale(SaleDto saleDto, Client client) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException, SaleNotValidPaymentMethodException {

        double totalValue = saleHelper.calculateTotalValue(saleDto);
        String description = saleHelper.generateSaleDescription(saleDto, client);
        int totalQuantity = saleDto.quantity().stream().mapToInt(Integer::intValue).sum();

        LocalDateTime saleDate = LocalDateTime.now();
        DateUtil.formatDate(saleDate);

        Sale sale = new Sale(saleDto, totalValue, description, totalQuantity, saleDate);

        saleHelper.validatePaymentMethod(sale, sale.getPaymentMethod());

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);

            Product product;
            product = saleHelper.validationProduct(productId, quantity);
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

    @Transactional
    public Sale statusSaleCanceled(Long id, Status status) {
        Sale sale = saleRepository.getReferenceById(id);
        sale.setStatus(status.CANCELLED);
        return saleRepository.save(sale);
    }
}


