package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.dto.ProductDto;
import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.dto.SaleListingDto;
import com.api.rest.conveniencestore.enums.Category;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.ClientCpfNotFoundException;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.model.SaleItem;
import com.api.rest.conveniencestore.repository.ClientRepository;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.repository.SaleItemRepository;
import com.api.rest.conveniencestore.repository.SaleRepository;
import com.api.rest.conveniencestore.service.LoyaltyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private LoyaltyService loyaltyService;

    @InjectMocks
    private SaleService saleService;

    private Product product;
    private Client client;
    private Sale sale;

    @BeforeEach
    void setUp() {
        saleService.setSaleHelper();  // inicializa SaleHelper com o productRepository mockado

        product = new Product(new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30)));
        client = new Client(new ClientDto("Maria Silva", "123.456.789-09"));
        sale = new Sale(new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09", null),
                10.0, "desc", 2, LocalDateTime.now(), "testuser");
    }

    @Test
    void registerSale_ShouldCreateSaleAndDecrementStock() throws Exception {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        SaleDto dto = new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09", null);

        when(clientRepository.findByCpf("123.456.789-09")).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(saleItemRepository.save(any(SaleItem.class))).thenReturn(null);

        Sale result = saleService.registerSale(dto);

        assertThat(result).isNotNull();
        assertThat(product.getStockQuantity()).isEqualTo(98); // 100 - 2 vendidos
        verify(productRepository).save(any(Product.class));
        verify(saleRepository, atLeastOnce()).save(any(Sale.class));
    }

    @Test
    void registerSale_WhenClientNotFound_ShouldThrow() {
        SaleDto dto = new SaleDto(List.of(1L), List.of(1), PaymentMethod.CASH, "000.000.000-00", null);
        when(clientRepository.findByCpf("000.000.000-00")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.registerSale(dto))
                .isInstanceOf(ClientCpfNotFoundException.class);
    }

    @Test
    void registerSale_WhenListsSizeMismatch_ShouldThrow() {
        SaleDto dto = new SaleDto(List.of(1L, 2L), List.of(1), PaymentMethod.CASH, "123.456.789-09", null);

        assertThatThrownBy(() -> saleService.registerSale(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void listSalesByPaymentMethod_ShouldReturnFilteredSales() {
        Pageable pageable = PageRequest.of(0, 10);
        when(saleRepository.findByPaymentMethod(PaymentMethod.CASH, pageable)).thenReturn(new PageImpl<>(List.of(sale)));

        Page<SaleListingDto> result = saleService.listSalesByPaymentMethod(PaymentMethod.CASH, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listSalesByPaymentMethod_WhenNone_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(saleRepository.findByPaymentMethod(PaymentMethod.DEBIT, pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<SaleListingDto> result = saleService.listSalesByPaymentMethod(PaymentMethod.DEBIT, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void statusSaleCanceled_ShouldSetCancelledStatusAndRestoreStock() {
        product.setStockQuantity(98); // simula estoque após venda de 2 unidades
        SaleItem saleItem = new SaleItem(sale, 1L, 2);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySaleId(1L)).thenReturn(List.of(saleItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.statusSaleCanceled(1L, Status.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(product.getStockQuantity()).isEqualTo(100); // 98 + 2 restaurados
        verify(productRepository).save(product);
        verify(saleRepository).save(sale);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(saleRepository.existsById(1L)).thenReturn(true);

        assertThat(saleService.existsById(1L)).isTrue();
    }
}
