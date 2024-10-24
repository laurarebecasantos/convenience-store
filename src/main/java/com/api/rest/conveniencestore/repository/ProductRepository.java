package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.enums.Category;
import com.api.rest.conveniencestore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Collection<Product> findByCategory(Category category);

    boolean existsByName(String name);

    boolean existsById(Long id);

    List<Product> findByExpirationDate(LocalDate expirationDate);
}
