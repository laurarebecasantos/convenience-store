package com.api.rest.conveniencestore.sale.service;

import com.api.rest.conveniencestore.client.dto.ClientDto;
import com.api.rest.conveniencestore.product.dto.ProductDto;
import com.api.rest.conveniencestore.sale.dto.SaleDto;
import com.api.rest.conveniencestore.shared.enums.Category;
import com.api.rest.conveniencestore.shared.enums.PaymentMethod;
import com.api.rest.conveniencestore.shared.enums.Status;
import com.api.rest.conveniencestore.shared.exception.ProductInactiveException;
import com.api.rest.conveniencestore.shared.exception.ProductInsufficientStockException;
import com.api.rest.conveniencestore.shared.exception.ProductNotFoundException;
import com.api.rest.conveniencestore.shared.exception.SaleNotValidPaymentMethodException;
import com.api.rest.conveniencestore.client.model.Client;
import com.api.rest.conveniencestore.product.model.Product;
import com.api.rest.conveniencestore.sale.model.Sale;
import com.api.rest.conveniencestore.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleHelperTest {

    @Mock
    private ProductRepository productRepository;

    private SaleHelper saleHelper;
    private Product product;
    private Client client;

    @BeforeEach
    void setUp() {
        saleHelper = new SaleHelper(productRepository);
        product = new Product(new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30)));
        client = new Client(new ClientDto("Maria Silva", "123.456.789-09"));
    }

    @Test
    void calculateTotalValue_ShouldReturnCorrectSum() throws ProductNotFoundException {
        SaleDto dto = new SaleDto(List.of(1L, 2L), List.of(2, 3), PaymentMethod.CASH, "123.456.789-09", null);
        Product p2 = new Product(new ProductDto("Agua", Category.BEVERAGE, 2.0, 50, LocalDate.now().plusDays(30)));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));

        double total = saleHelper.calculateTotalValue(dto);

        assertThat(total).isEqualTo(5.0 * 2 + 2.0 * 3); // 10 + 6 = 16
    }

    @Test
    void calculateTotalValue_WhenProductNotFound_ShouldThrow() {
        SaleDto dto = new SaleDto(List.of(99L), List.of(1), PaymentMethod.CASH, "123.456.789-09", null);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleHelper.calculateTotalValue(dto))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void generateSaleDescription_ShouldContainCpfAndProductName() throws ProductNotFoundException {
        SaleDto dto = new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09", null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String description = saleHelper.generateSaleDescription(dto, client, "testuser");

        assertThat(description).contains("123.456.789-09");
        assertThat(description).contains("Coca-Cola");
    }

    @Test
    void validationProduct_WhenValid_ShouldReturnProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = saleHelper.validationProduct(1L, 5);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Coca-Cola");
    }

    @Test
    void validationProduct_WhenNotFound_ShouldThrow() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> saleHelper.validationProduct(99L, 1))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void validationProduct_WhenInactive_ShouldThrow() {
        product.setStatus(Status.INACTIVE);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> saleHelper.validationProduct(1L, 1))
                .isInstanceOf(ProductInactiveException.class);
    }

    @Test
    void validationProduct_WhenInsufficientStock_ShouldThrow() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> saleHelper.validationProduct(1L, 999))
                .isInstanceOf(ProductInsufficientStockException.class);
    }

    @Test
    void validatePaymentMethod_WhenNull_ShouldThrow() {
        Sale sale = new Sale(new SaleDto(List.of(), List.of(), PaymentMethod.CASH, "cpf", null), 0, "", 0, LocalDateTime.now(), "testuser");

        assertThatThrownBy(() -> saleHelper.validatePaymentMethod(sale, null))
                .isInstanceOf(SaleNotValidPaymentMethodException.class);
    }

    @Test
    void validatePaymentMethod_WhenMatches_ShouldNotThrow() {
        Sale sale = new Sale(new SaleDto(List.of(), List.of(), PaymentMethod.CASH, "cpf", null), 0, "", 0, LocalDateTime.now(), "testuser");

        assertThatCode(() -> saleHelper.validatePaymentMethod(sale, PaymentMethod.CASH))
                .doesNotThrowAnyException();
    }
}
