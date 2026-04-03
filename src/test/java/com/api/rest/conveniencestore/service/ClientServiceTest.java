package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.dto.ClientListingDto;
import com.api.rest.conveniencestore.exceptions.ClientCpfNotFoundException;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.repository.ClientRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(new ClientDto("Maria Silva", "123.456.789-09"));
    }

    @Test
    void registerClient_ShouldSaveAndReturn() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.registerClient(new ClientDto("Maria Silva", "123.456.789-09"));

        assertThat(result).isNotNull();
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void listClients_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clientRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(client)));

        Page<ClientListingDto> result = clientService.listClients(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listClients_WhenEmpty_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clientRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<ClientListingDto> result = clientService.listClients(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findById_WhenExists_ShouldReturn() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client result = clientService.findById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(99L))
                .isInstanceOf(ClientCpfNotFoundException.class);
    }

    @Test
    void findByCpf_WhenExists_ShouldReturn() {
        when(clientRepository.findByCpf("123.456.789-09")).thenReturn(Optional.of(client));

        Client result = clientService.findByCpf("123.456.789-09");

        assertThat(result.getCpf()).isEqualTo("123.456.789-09");
    }

    @Test
    void findByCpf_WhenNotFound_ShouldThrow() {
        when(clientRepository.findByCpf("000.000.000-00")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findByCpf("000.000.000-00"))
                .isInstanceOf(ClientCpfNotFoundException.class);
    }

    @Test
    void existsByCpf_WhenExists_ShouldReturnTrue() {
        when(clientRepository.existsByCpf("123.456.789-09")).thenReturn(true);

        assertThat(clientService.existsByCpf("123.456.789-09")).isTrue();
    }

    @Test
    void existsByName_WhenNotExists_ShouldReturnFalse() {
        when(clientRepository.existsByName("Joao")).thenReturn(false);

        assertThat(clientService.existsByName("Joao")).isFalse();
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        assertThat(clientService.existsById(1L)).isTrue();
    }
}
