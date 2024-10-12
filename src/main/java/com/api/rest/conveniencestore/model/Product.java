package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.dto.ProductDto;
import com.api.rest.conveniencestore.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.enums.Category;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.utils.StatusUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "products")
@Entity(name = "Product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product implements StatusUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Category cannot be null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    @NotNull(message = "Price cannot be null")
    private double price;

    @Column(nullable = false, name = "stock_quantity")
    @NotNull(message = "Stock Quantity cannot be null")
    private int stockQuantity;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Product(ProductDto data) {
        this.name = data.name();
        this.category = data.category();
        this.price = data.price();
        this.stockQuantity = data.stockQuantity();
        this.expirationDate = data.expirationDate();
        this.status = Status.REGISTERED;
    }

    public void productUpdateData(ProductUpdateDto updateDto) {
        if (updateDto.price() != null) {
            this.price = updateDto.price();
        }
        if (updateDto.stockQuantity() != null) {
            this.stockQuantity = updateDto.stockQuantity();
        }
        if (updateDto.expirationDate() != null) {
            this.expirationDate = updateDto.expirationDate();
        }
        if (updateDto.status() != null) {
            this.status = updateDto.status();
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
