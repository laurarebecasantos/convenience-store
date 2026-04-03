package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.ClientDto;
import com.api.rest.conveniencestore.dto.ClientListingDto;
import com.api.rest.conveniencestore.exceptions.CpfValidateException;
import com.api.rest.conveniencestore.exceptions.NameValidateException;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.repository.ClientRepository;
import com.api.rest.conveniencestore.service.ClientService;
import com.api.rest.conveniencestore.utils.MessageConstants;
import com.api.rest.conveniencestore.validations.CpfValidator;
import com.api.rest.conveniencestore.validations.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserValidator userValidator;

    @PostMapping
public ResponseEntity<Client> register(@Valid @RequestBody ClientDto clientDto) throws CpfValidateException, NameValidateException {
        userValidator.validateNameClient(clientDto.name());

        if (clientRepository.existsByCpf(clientDto.cpf())) {
            throw new CpfValidateException(MessageConstants.CPF_ALREADY_EXISTS + clientDto.cpf());
        }
        CpfValidator.validateCpf(clientDto.cpf());

        Client savedClient = clientService.registerClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @GetMapping
    public ResponseEntity<Page<ClientListingDto>> list(Pageable pageable) {
        Page<ClientListingDto> clients = clientService.listClients(pageable);
        if (clients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientListingDto> findById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(new ClientListingDto(client));
    }
}
