package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.dto.ClientDto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(name = "Clients")
@Entity(name = "Client")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String cpf;

    public Client(ClientDto data) {
        this.cpf = data.cpf();
        this.name = data.name();
    }

    public void setCpf(Client client) {
        this.cpf = cpf;
    }

    public void setName(Client client) {
        this.name = name;
    }

}