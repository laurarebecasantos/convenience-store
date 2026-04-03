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
import com.api.rest.conveniencestore.service.ProductService;
import com.api.rest.conveniencestore.utils.MessageConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
public ResponseEntity<Product> register(@Valid @RequestBody ProductDto productDto) throws UserRegistrationException {
        if (productService.existsByName(productDto.name())) {
            throw new UserRegistrationException(MessageConstants.PRODUCT_ALREADY_EXISTS + productDto.name());
        }
        Product savedProduct = productService.registerProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<Page<ProductListingDto>> list(Pageable pageable) {
        return ResponseEntity.ok(productService.listProducts(pageable));
    }

    @PutMapping("/{id}")
public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto updateDto) throws ProductNotFoundException {
        if (!productService.existsById(id)) {
            throw new ProductNotFoundException((String.format(MessageConstants.PRODUCT_NOT_FOUND, id)));
        }
        Product updatedProduct = productService.updateProduct(id, updateDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/status")
public ResponseEntity<Product> status(@PathVariable Long id, @Valid @RequestBody Map<String, String> statusRequest) throws ProductNotFoundException, ProductInvalidStatusException {
        String statusString = statusRequest.get("status");
        Status statusInactive;
        try {
            statusInactive = Status.fromValueStatus(statusString);
        } catch (IllegalArgumentException e) {
            throw new ProductInvalidStatusException(MessageConstants.INVALID_STATUS + statusString);
        }

        if (!Status.INACTIVE.equals(statusInactive) && !Status.ACTIVE.equals(statusInactive)) {
            throw new ProductInvalidStatusException(MessageConstants.STATUS_ACTIVE_OR_INACTIVE_PRODUCT);
        }

        if (!productService.existsById(id)) {
            throw new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, id));
        }

        Product updatedStatusProduct = productService.updateProductStatus(id, statusInactive);
        return ResponseEntity.ok(updatedStatusProduct);
    }


    @GetMapping("/duedate")
    public ResponseEntity<List<Product>> listarProdutosVencidos() {
        List<Product> productsExpiring = productService.searchExpiredProducts();
        if (productsExpiring.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productsExpiring);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Product>> listProductsNearExpiration(@RequestParam(defaultValue = "7") int days) {
        List<Product> products = productService.searchProductsNearExpiration(days);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
}
