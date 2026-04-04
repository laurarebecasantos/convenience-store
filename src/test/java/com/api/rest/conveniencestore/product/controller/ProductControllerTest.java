package com.api.rest.conveniencestore.product.controller;

import com.api.rest.conveniencestore.product.dto.ProductDto;
import com.api.rest.conveniencestore.product.dto.ProductListingDto;
import com.api.rest.conveniencestore.product.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.shared.enums.Category;
import com.api.rest.conveniencestore.shared.enums.Status;
import com.api.rest.conveniencestore.product.model.Product;
import com.api.rest.conveniencestore.product.repository.ProductRepository;
import com.api.rest.conveniencestore.user.repository.UserRepository;
import com.api.rest.conveniencestore.product.service.ProductService;
import com.api.rest.conveniencestore.user.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_WhenValidProduct_ShouldReturn201() throws Exception {
        when(productService.existsByName("Coca-Cola")).thenReturn(false);
        when(productService.registerProduct(any())).thenReturn(product);

        ProductDto dto = new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30));

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_WhenNameAlreadyExists_ShouldReturn409() throws Exception {
        when(productService.existsByName("Coca-Cola")).thenReturn(true);

        ProductDto dto = new ProductDto("Coca-Cola", Category.BEVERAGE, 5.0, 100, LocalDate.now().plusDays(30));

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void list_WhenProductsExist_ShouldReturn200() throws Exception {
        Page<ProductListingDto> page = new PageImpl<>(List.of(new ProductListingDto(product)));
        when(productService.listProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Coca-Cola"));
    }

    @Test
    @WithMockUser
    void list_WhenNoProducts_ShouldReturnEmptyList() throws Exception {
        Page<ProductListingDto> page = new PageImpl<>(List.of());
        when(productService.listProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_WhenProductExists_ShouldReturn200() throws Exception {
        when(productService.existsById(1L)).thenReturn(true);
        when(productService.updateProduct(eq(1L), any())).thenReturn(product);

        ProductUpdateDto dto = new ProductUpdateDto(6.0, 50, LocalDate.now().plusDays(60), Status.ACTIVE);

        mockMvc.perform(put("/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(productService.existsById(99L)).thenReturn(false);

        ProductUpdateDto dto = new ProductUpdateDto(6.0, 50, LocalDate.now().plusDays(60), Status.ACTIVE);

        mockMvc.perform(put("/products/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void status_WhenSetInactive_ShouldReturn200() throws Exception {
        when(productService.existsById(1L)).thenReturn(true);
        when(productService.updateProductStatus(eq(1L), eq(Status.INACTIVE))).thenReturn(product);

        mockMvc.perform(patch("/products/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "INACTIVE"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void duedate_WhenExpiredProductsExist_ShouldReturn200() throws Exception {
        when(productService.searchExpiredProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products/duedate"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void duedate_WhenNoExpiredProducts_ShouldReturn204() throws Exception {
        when(productService.searchExpiredProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products/duedate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void list_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());
    }
}
