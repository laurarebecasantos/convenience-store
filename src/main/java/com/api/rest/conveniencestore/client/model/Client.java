package com.api.rest.conveniencestore.client.model;

import com.api.rest.conveniencestore.client.dto.ClientDto;
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

    @Column(name = "points_balance", nullable = false)
    private int pointsBalance = 0;

    public Client(ClientDto data) {
        this.cpf = data.cpf();
        this.name = data.name();
        this.pointsBalance = 0;
    }

    public void addPoints(int points) {
        this.pointsBalance += points;
    }

    public void deductPoints(int points) {
        this.pointsBalance -= points;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setName(String name) {
        this.name = name;
    }

}