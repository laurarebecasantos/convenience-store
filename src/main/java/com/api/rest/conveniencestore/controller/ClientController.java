package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.service.ClientService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @PostMapping
    @Transactional
    public ResponseEntity<Client> register(@Valid @RequestBody ClientDto clientDto) {
        Client savedClient = clientService.registerClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }
}
