package com.api.rest.conveniencestore.client.service;

import com.api.rest.conveniencestore.client.dto.ClientDto;
import com.api.rest.conveniencestore.client.dto.ClientListingDto;
import com.api.rest.conveniencestore.shared.exception.ClientCpfNotFoundException;
import com.api.rest.conveniencestore.client.model.Client;
import com.api.rest.conveniencestore.client.repository.ClientRepository;
import com.api.rest.conveniencestore.shared.utils.MessageConstants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public boolean existsByCpf(String cpf) {
        return clientRepository.existsByCpf(cpf);
    }

    public boolean existsByName(String name) {
        return clientRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }

    @Transactional
    public Client registerClient(ClientDto clientDto) {
        return clientRepository.save(new Client(clientDto));
    }

    public Page<ClientListingDto> listClients(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(ClientListingDto::new);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientCpfNotFoundException(
                        String.format(MessageConstants.CLIENT_NOT_FOUND_BY_ID, id)));
    }

    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClientCpfNotFoundException(
                        MessageConstants.CLIENT_NOT_FOUND_BY_CPF + cpf));
    }
}
