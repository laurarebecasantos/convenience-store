package com.api.rest.conveniencestore.client.controller;

import com.api.rest.conveniencestore.client.dto.ClientDto;
import com.api.rest.conveniencestore.client.dto.ClientListingDto;
import com.api.rest.conveniencestore.client.model.Client;
import com.api.rest.conveniencestore.client.repository.ClientRepository;
import com.api.rest.conveniencestore.user.repository.UserRepository;
import com.api.rest.conveniencestore.client.service.ClientService;
import com.api.rest.conveniencestore.user.service.TokenService;
import com.api.rest.conveniencestore.shared.validation.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.api.rest.conveniencestore.security.ConfigurationSecurity;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(ConfigurationSecurity.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(new ClientDto("Maria Silva", "123.456.789-09"));
    }

    @Test
    @WithMockUser
    void register_WhenValidClient_ShouldReturn201() throws Exception {
        when(clientRepository.existsByCpf("123.456.789-09")).thenReturn(false);
        when(clientService.registerClient(any())).thenReturn(client);

        ClientDto dto = new ClientDto("Maria Silva", "123.456.789-09");

        mockMvc.perform(post("/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void register_WhenCpfAlreadyExists_ShouldReturn409() throws Exception {
        when(clientRepository.existsByCpf("123.456.789-09")).thenReturn(true);

        ClientDto dto = new ClientDto("Maria Silva", "123.456.789-09");

        mockMvc.perform(post("/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void list_WhenClientsExist_ShouldReturn200() throws Exception {
        ClientListingDto dto = new ClientListingDto(client);
        Page<ClientListingDto> page = new PageImpl<>(List.of(dto));
        when(clientService.listClients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Maria Silva"));
    }

    @Test
    @WithMockUser
    void list_WhenNoClients_ShouldReturn204() throws Exception {
        Page<ClientListingDto> page = new PageImpl<>(List.of());
        when(clientService.listClients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void findById_WhenClientExists_ShouldReturn200() throws Exception {
        when(clientService.findById(1L)).thenReturn(client);

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));
    }

    @Test
    void list_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isForbidden());
    }
}
