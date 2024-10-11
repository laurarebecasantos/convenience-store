package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.ProductDto;
import com.api.rest.conveniencestore.dto.ProductListingDto;
import com.api.rest.conveniencestore.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Transactional
    public Product registerProduct(ProductDto productDto) {
        Product product = new Product(productDto);
        return productRepository.save(product);
    }

    public List<ProductListingDto> listProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductListingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateDto updateDto) {
        Product product = productRepository.getReferenceById(id);
        product.productUpdateData(updateDto);
        return productRepository.save(product);
    }

    @Transactional
    public Product statusProductInactive(Long id, Status status) {
        Product product = productRepository.getReferenceById(id);
        product.setStatus(status.INACTIVE);
        return productRepository.save(product);
    }

    public List<Product> searchExpiredProducts() {
        LocalDate dateNow = LocalDate.now();
        return productRepository.findByExpirationDate(dateNow);
    }
}

