package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.ProductDto;
import com.api.rest.conveniencestore.dto.ProductListingDto;
import com.api.rest.conveniencestore.dto.ProductUpdateDto;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.ProductDateInvalidException;
import com.api.rest.conveniencestore.exceptions.ProductInactiveException;
import com.api.rest.conveniencestore.exceptions.ProductNotFoundException;
import com.api.rest.conveniencestore.model.Product;
import com.api.rest.conveniencestore.repository.ProductRepository;
import com.api.rest.conveniencestore.utils.MessageConstants;
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
        if (productDto.price() <= 0) {
            throw new ProductDateInvalidException(MessageConstants.INVALID_PRICE);
        }
        if (productDto.stockQuantity() < 0) {
            throw new ProductDateInvalidException(MessageConstants.INVALID_STOCK);
        }
        if (productDto.expirationDate() != null && productDto.expirationDate().isBefore(LocalDate.now())) {
            throw new ProductDateInvalidException(MessageConstants.INVALID_EXPIRATION_DATE);
        }
        return productRepository.save(new Product(productDto));
    }

    public List<ProductListingDto> listProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductListingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateDto updateDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, id)));

        if (product.isExpired()) {
            throw new ProductDateInvalidException(MessageConstants.PRODUCT_EXPIRED_UPDATE);
        }
        if (product.getStatus() == Status.INACTIVE) {
            throw new ProductInactiveException(MessageConstants.PRODUCT_INACTIVE_UPDATE);
        }

        product.productUpdateData(updateDto);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProductStatus(Long id, Status status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(MessageConstants.PRODUCT_NOT_FOUND, id)));
        product.setStatus(status);
        return productRepository.save(product);
    }

    @Transactional
    public Product statusProductInactive(Long id, Status status) {
        return updateProductStatus(id, status);
    }

    public List<Product> searchExpiredProducts() {
        return productRepository.findByExpirationDateLessThanEqual(LocalDate.now());
    }

    public List<Product> searchProductsNearExpiration(int days) {
        LocalDate today = LocalDate.now();
        return productRepository.findByExpirationDateBetween(today.plusDays(1), today.plusDays(days));
    }
}
