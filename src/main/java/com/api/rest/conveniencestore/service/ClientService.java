package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public boolean existsByCpf(String cpf) {
        return clientRepository.existsById(Long.valueOf(cpf));
    }

    public boolean existsByName(String name) {
        return clientRepository.existsByName(name);
    }

    @Transactional
    public Client registerClient(ClientDto clientDto) {
        Client client = new Client(clientDto);
        return clientRepository.save(client);
    }
}