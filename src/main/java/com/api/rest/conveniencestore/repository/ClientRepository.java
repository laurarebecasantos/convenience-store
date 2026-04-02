package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByName(String name);

    Optional<Client> findByCpf(String cpf);
}