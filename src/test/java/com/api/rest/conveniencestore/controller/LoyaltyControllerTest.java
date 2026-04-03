package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.dto.LoyaltySimulateDto;
import com.api.rest.conveniencestore.dto.LoyaltySimulateResponseDto;
import com.api.rest.conveniencestore.dto.LoyaltyTransactionDto;
import com.api.rest.conveniencestore.enums.TransactionType;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.repository.UserRepository;
import com.api.rest.conveniencestore.security.ConfigurationSecurity;
import com.api.rest.conveniencestore.service.ClientService;
import com.api.rest.conveniencestore.service.LoyaltyService;
import com.api.rest.conveniencestore.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoyaltyController.class)
@Import(ConfigurationSecurity.class)
class LoyaltyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoyaltyService loyaltyService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    // === POST /loyalty/simulate ===

    @Test
    @WithMockUser
    void simulate_ShouldReturn200WithProjection() throws Exception {
        Client client = new Client(new ClientDto("Maria", "123.456.789-09"));
        LoyaltySimulateResponseDto response = new LoyaltySimulateResponseDto(500, 5.0, 195.0, 695);

        when(clientService.findById(1L)).thenReturn(client);
        when(loyaltyService.simulate(any(Client.class), eq(200.0), eq(500))).thenReturn(response);

        LoyaltySimulateDto dto = new LoyaltySimulateDto(1L, 200.0, 500);

        mockMvc.perform(post("/loyalty/simulate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").value(5.0))
                .andExpect(jsonPath("$.finalAmount").value(195.0))
                .andExpect(jsonPath("$.pointsToUse").value(500))
                .andExpect(jsonPath("$.pointsAfterPurchase").value(695));
    }

    @Test
    void simulate_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        LoyaltySimulateDto dto = new LoyaltySimulateDto(1L, 200.0, 500);

        mockMvc.perform(post("/loyalty/simulate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // === GET /loyalty/clients/{id}/transactions ===

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTransactions_ShouldReturn200WithList() throws Exception {
        LoyaltyTransactionDto tx = new LoyaltyTransactionDto(1L, 100, TransactionType.EARN, 1L, LocalDateTime.now());
        Page<LoyaltyTransactionDto> page = new PageImpl<>(List.of(tx));

        when(loyaltyService.getTransactions(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/loyalty/clients/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].points").value(100))
                .andExpect(jsonPath("$.content[0].type").value("EARN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTransactions_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/loyalty/clients/1/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransactions_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/loyalty/clients/1/transactions"))
                .andExpect(status().isForbidden());
    }
}
