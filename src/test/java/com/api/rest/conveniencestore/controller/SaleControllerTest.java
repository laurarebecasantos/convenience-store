package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.dto.SaleListingDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.Sale;
import com.api.rest.conveniencestore.repository.SaleRepository;
import com.api.rest.conveniencestore.security.JwtAuthenticationFilter;
import com.api.rest.conveniencestore.service.SaleService;
import com.api.rest.conveniencestore.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SaleService saleService;

    @MockBean
    private SaleRepository saleRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Sale sale;

    @BeforeEach
    void setUp() {
        SaleDto dto = new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09");
        sale = new Sale(dto, 10.0, "desc", 2, LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void register_WhenValidSale_ShouldReturn201() throws Exception {
        when(saleService.registerSale(any())).thenReturn(sale);

        SaleDto dto = new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09");

        mockMvc.perform(post("/sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void listByPaymentMethod_WhenSalesExist_ShouldReturn200() throws Exception {
        SaleListingDto listingDto = new SaleListingDto(sale);
        when(saleService.listSalesByPaymentMethod(PaymentMethod.CASH)).thenReturn(List.of(listingDto));

        mockMvc.perform(get("/sales").param("paymentMethod", "CASH"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void listByPaymentMethod_WhenInvalidMethod_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/sales").param("paymentMethod", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void status_WhenCancelSale_ShouldReturn200() throws Exception {
        when(saleService.existsById(1L)).thenReturn(true);
        when(saleService.statusSaleCanceled(eq(1L), eq(Status.CANCELLED))).thenReturn(sale);

        mockMvc.perform(patch("/sales/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "CANCELLED"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void status_WhenInvalidStatus_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/sales/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "APPROVED"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void status_WhenSaleNotFound_ShouldReturn404() throws Exception {
        when(saleService.existsById(99L)).thenReturn(false);

        mockMvc.perform(patch("/sales/99/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "CANCELLED"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        SaleDto dto = new SaleDto(List.of(1L), List.of(2), PaymentMethod.CASH, "123.456.789-09");

        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
