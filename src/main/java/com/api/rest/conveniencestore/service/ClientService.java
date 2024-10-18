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

    @Transactional
    public Client registerClient(ClientDto clientDto) {
        Client client = new Client(clientDto);
        return clientRepository.save(client);
    }
}

