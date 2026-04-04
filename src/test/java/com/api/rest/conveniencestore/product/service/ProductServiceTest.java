package com.api.rest.conveniencestore.product.service;

import com.api.rest.conveniencestore.product.dto.ProductDto;
import com.api.rest.conveniencestore.product.dto.ProductListingDto;
import com.api.rest.conveniencestore.product.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.shared.enums.Category;
import com.api.rest.conveniencestore.shared.enums.Status;
import com.api.rest.conveniencestore.shared.exception.ProductDateInvalidException;
import com.api.rest.conveniencestore.shared.exception.ProductInactiveException;
import com.api.rest.conveniencestore.product.model.Product;
import com.api.rest.conveniencestore.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30)));
    }

    @Test
    void registerProduct_ShouldSaveAndReturn() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.registerProduct(
                new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30)));

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void listProducts_ShouldReturnAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductListingDto> result = productService.listProducts(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findAll(pageable);
    }

    @Test
    void listProducts_WhenEmpty_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<ProductListingDto> result = productService.listProducts(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void updateProduct_ShouldCallSave() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(1L,
                new ProductUpdateDto(6.0, 50, LocalDate.now().plusDays(60), Status.ACTIVE));

        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void updateProductStatus_ToInactive_ShouldSetStatusAndSave() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProductStatus(1L, Status.INACTIVE);

        assertThat(result.getStatus()).isEqualTo(Status.INACTIVE);
        verify(productRepository).save(product);
    }

    @Test
    void updateProductStatus_ToActive_ShouldSetStatusAndSave() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProductStatus(1L, Status.ACTIVE);

        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void searchExpiredProducts_ShouldReturnTodayExpiredProducts() {
        when(productRepository.findByExpirationDateLessThanEqual(any(LocalDate.class))).thenReturn(List.of(product));

        List<Product> result = productService.searchExpiredProducts();

        assertThat(result).hasSize(1);
    }

    @Test
    void registerProduct_WhenPriceIsZero_ShouldThrow() {
        assertThatThrownBy(() -> productService.registerProduct(
                new ProductDto("Produto", Category.BEVERAGE, 0.0, 10, LocalDate.now().plusDays(10))))
                .isInstanceOf(ProductDateInvalidException.class);
    }

    @Test
    void registerProduct_WhenStockIsNegative_ShouldThrow() {
        assertThatThrownBy(() -> productService.registerProduct(
                new ProductDto("Produto", Category.BEVERAGE, 5.0, -1, LocalDate.now().plusDays(10))))
                .isInstanceOf(ProductDateInvalidException.class);
    }

    @Test
    void registerProduct_WhenExpirationDateIsInPast_ShouldThrow() {
        assertThatThrownBy(() -> productService.registerProduct(
                new ProductDto("Produto", Category.BEVERAGE, 5.0, 10, LocalDate.now().minusDays(1))))
                .isInstanceOf(ProductDateInvalidException.class);
    }

    @Test
    void updateProduct_WhenProductIsExpired_ShouldThrow() {
        product.setExpirationDate(LocalDate.now().minusDays(1));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(1L,
                new ProductUpdateDto(6.0, 50, LocalDate.now().plusDays(60), Status.ACTIVE)))
                .isInstanceOf(ProductDateInvalidException.class);
    }

    @Test
    void updateProduct_WhenProductIsInactive_ShouldThrow() {
        product.setStatus(Status.INACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(1L,
                new ProductUpdateDto(6.0, 50, LocalDate.now().plusDays(60), Status.ACTIVE)))
                .isInstanceOf(ProductInactiveException.class);
    }

    @Test
    void existsByName_WhenExists_ShouldReturnTrue() {
        when(productRepository.existsByName("Coca-Cola")).thenReturn(true);

        assertThat(productService.existsByName("Coca-Cola")).isTrue();
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertThat(productService.existsById(1L)).isTrue();
    }
}
