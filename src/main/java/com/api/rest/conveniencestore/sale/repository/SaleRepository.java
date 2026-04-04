package com.api.rest.conveniencestore.sale.repository;

import com.api.rest.conveniencestore.shared.enums.PaymentMethod;
import com.api.rest.conveniencestore.sale.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

     Collection<Sale> findByPaymentMethod(PaymentMethod paymentMethod);

     Page<Sale> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

     boolean existsById(Long id);
}