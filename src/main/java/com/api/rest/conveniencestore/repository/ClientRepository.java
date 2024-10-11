package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsById(Long id);

    boolean existsByCpf(String cpf);

}