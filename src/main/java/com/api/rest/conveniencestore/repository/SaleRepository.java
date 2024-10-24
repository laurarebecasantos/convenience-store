package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

     Collection<Sale> findByPaymentMethod(PaymentMethod paymentMethod);

     boolean existsById(Long id);
}