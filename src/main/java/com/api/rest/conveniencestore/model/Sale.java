package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.dto.SaleDto;
import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.utils.StatusUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "sales")
@Entity(name = "Sale")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sale implements StatusUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, name = "total_value")
    @NotNull(message = "Total Value cannot be null")
    private double totalValue;

    @Column(nullable = false, name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = false, name = "date_sale")
    private LocalDateTime saleDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "seller")
    private String seller;

    @Column(name = "points_earned", nullable = false)
    private int pointsEarned = 0;

    @Column(name = "points_used", nullable = false)
    private int pointsUsed = 0;

    @Column(nullable = false)
    private double discount = 0.0;

    public Sale(SaleDto saleDto, double totalValue, String description, int quantity, LocalDateTime saleDate, String seller) {
        this.description = description;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.paymentMethod = saleDto.paymentMethod();
        this.status = Status.APPROVED;
        this.saleDate = LocalDateTime.now();
        this.seller = seller;
        this.pointsEarned = 0;
        this.pointsUsed = 0;
        this.discount = 0.0;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public void setPointsUsed(int pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}