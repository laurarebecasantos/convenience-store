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

    @Autowired
    private LoyaltyService loyaltyService;

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

        if (saleDto.productIds().size() != saleDto.quantity().size()) {
            throw new IllegalArgumentException(com.api.rest.conveniencestore.utils.MessageConstants.SALE_LISTS_SIZE_MISMATCH);
        }

        Client client = clientRepository.findByCpf(saleDto.clientCpf())
                .orElseThrow(() -> new ClientCpfNotFoundException(
                        com.api.rest.conveniencestore.utils.MessageConstants.CLIENT_NOT_FOUND_BY_CPF + saleDto.clientCpf()));

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String seller = authenticatedUser.getUsername();

        double totalValue = saleHelper.calculateTotalValue(saleDto);

        // aplica desconto por pontos, se solicitado
        double discount = 0.0;
        int pointsUsed = 0;
        if (saleDto.pointsToUse() != null && saleDto.pointsToUse() > 0) {
            discount = loyaltyService.redeemPoints(client, saleDto.pointsToUse(), totalValue, null);
            pointsUsed = saleDto.pointsToUse();
        }

        double finalValue = totalValue - discount;
        String description = saleHelper.generateSaleDescription(saleDto, client, seller);
        int totalQuantity = saleDto.quantity().stream().mapToInt(Integer::intValue).sum();

        Sale sale = new Sale(saleDto, finalValue, description, totalQuantity, LocalDateTime.now(), seller);
        sale.setDiscount(discount);
        sale.setPointsUsed(pointsUsed);

        saleHelper.validatePaymentMethod(sale, sale.getPaymentMethod());

        Sale savedSale = saleRepository.save(sale);

        for (int i = 0; i < saleDto.productIds().size(); i++) {
            Long productId = saleDto.productIds().get(i);
            Integer quantity = saleDto.quantity().get(i);

            Product product = saleHelper.validationProduct(productId, quantity);
            int newStock = product.getStockQuantity() - quantity;
            if (newStock < 0) {
                throw new ProductInsufficientStockException(
                        com.api.rest.conveniencestore.utils.MessageConstants.STOCK_CANNOT_BE_NEGATIVE + product.getName());
            }
            product.setStockQuantity(newStock);
            productRepository.save(product);

            saleItemRepository.save(new SaleItem(savedSale, productId, quantity));
        }

        // acumula pontos sobre o valor final (após desconto)
        int pointsEarned = (int) Math.floor(finalValue);
        loyaltyService.earnPoints(client, finalValue, savedSale.getId());
        savedSale.setPointsEarned(pointsEarned);
        saleRepository.save(savedSale);

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

        // estorna pontos da venda (saldo pode ficar negativo — comportamento esperado)
        loyaltyService.cancelPoints(id);

        sale.setStatus(Status.CANCELLED);
        return saleRepository.save(sale);
    }
}


