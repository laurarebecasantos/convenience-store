package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.dto.SaleListingDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.*;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.model.SaleItem;
import com.api.rest.conveniencestore.repository.ClientRepository;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.repository.SaleItemRepository;
import com.api.rest.conveniencestore.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private SaleItemRepository saleItemRepository;

    private SaleHelper saleHelper;

    @Autowired
    public void setSaleHelper() {
        this.saleHelper = new SaleHelper(productRepository);
    }

    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }

    @Transactional
    public Sale registerSale(SaleDto saleDto) throws ProductNotFoundException, ProductInactiveException, ProductInsufficientStockException, SaleNotValidPaymentMethodException {

        Client client = clientRepository.findByCpf(saleDto.clientCpf())
                .orElseThrow(() -> new ClientCpfNotFoundException(
                        com.api.rest.conveniencestore.utils.MessageConstants.CLIENT_NOT_FOUND_BY_CPF + saleDto.clientCpf()));

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String seller = authenticatedUser.getUsername();

        double totalValue = saleHelper.calculateTotalValue(saleDto);
        String description = saleHelper.generateSaleDescription(saleDto, client, seller);
        int totalQuantity = saleDto.quantity().stream().mapToInt(Integer::intValue).sum();

        LocalDateTime saleDate = LocalDateTime.now();

        Sale sale = new Sale(saleDto, totalValue, description, totalQuantity, saleDate, seller);

        saleHelper.validatePaymentMethod(sale, sale.getPaymentMethod());

        Sale savedSale = saleRepository.save(sale);

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);

            Product product = saleHelper.validationProduct(productId, quantity);
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);

            saleItemRepository.save(new SaleItem(savedSale, productId, quantity));
        }
        return savedSale;
    }

    public List<SaleListingDto> listSalesByPaymentMethod(PaymentMethod payment) {
        return saleRepository.findByPaymentMethod(payment)
                .stream()
                .map(SaleListingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Sale statusSaleCanceled(Long id, Status status) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleListingNullException(com.api.rest.conveniencestore.utils.MessageConstants.SALE_NOT_FOUND));

        saleItemRepository.findBySaleId(id).forEach(item -> {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            });
        });

        sale.setStatus(Status.CANCELLED);
        return saleRepository.save(sale);
    }
}


