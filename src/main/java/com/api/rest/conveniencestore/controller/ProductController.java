package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.ProductDto;
import com.api.rest.conveniencestore.dto.ProductListingDto;
import com.api.rest.conveniencestore.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.ProductInvalidStatusException;
import com.api.rest.conveniencestore.exceptions.ProductNotFoundException;
import com.api.rest.conveniencestore.exceptions.UserListingNullException;
import com.api.rest.conveniencestore.exceptions.UserRegistrationException;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    public boolean existsById(Long id){
        return  productRepository.existsById(id);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Product> register(@Valid @RequestBody ProductDto productDto) throws UserRegistrationException {
        if (productService.existsByName(productDto.name())) {
            throw new UserRegistrationException("Product already registered with the name: " + productDto.name());
        }
        Product savedProduct = productService.registerProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductListingDto>> list() throws UserListingNullException {
        var returnProducts = productService.listProducts();
        if (returnProducts.isEmpty()) {
            throw new UserListingNullException("No registered products were found.");
        }
        return ResponseEntity.ok(returnProducts);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto updateDto) throws ProductNotFoundException {
        if (!productService.existsById(id)) {
            throw new ProductNotFoundException(("Product with ID: " + id + " not found."));
        }
        Product updatedProduct = productService.updateProduct(id, updateDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<Product> status(@PathVariable Long id, @Valid @RequestBody Map<String, String> statusRequest) throws ProductNotFoundException, ProductInvalidStatusException {
        String statusString = statusRequest.get("status");
        Status statusInactive;
        try {
            statusInactive = Status.fromValueStatus(statusString); // Converte a string para enum
        } catch (IllegalArgumentException e) {
            throw new ProductInvalidStatusException("Invalid status: " + statusString);
        }

        if (!Status.INACTIVE.equals(statusInactive)) {
            throw new ProductInvalidStatusException("The status can only be changed to INACTIVE.");
        }

        if (!productService.existsById(id)) {
            throw new ProductNotFoundException("Product with ID: " + id + " not found.");
        }

        Product updatedStatusProduct = productService.statusProductInactive(id, statusInactive);
        return ResponseEntity.ok(updatedStatusProduct);
    }

        @GetMapping("/duedate")
        public ResponseEntity<List<Product>> listExpiredProducts() {
            List<Product> productsExpiring = productService.searchExpiredProducts();
            if (productsExpiring.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(productsExpiring);
        }
    }


