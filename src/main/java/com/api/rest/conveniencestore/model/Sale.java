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

    public Sale(SaleDto saleDto, double totalValue, String description, int quantity, LocalDateTime saleDate) {
        this.description = description;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.paymentMethod = saleDto.paymentMethod();
        this.status = Status.APPROVED;
        this.saleDate = LocalDateTime.now();
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

