package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.LoyaltySimulateResponseDto;
import com.api.rest.conveniencestore.dto.LoyaltyTransactionDto;
import com.api.rest.conveniencestore.enums.TransactionType;
import com.api.rest.conveniencestore.exceptions.LoyaltyException;
import com.api.rest.conveniencestore.model.Client;
import com.api.rest.conveniencestore.model.LoyaltyPoint;
import com.api.rest.conveniencestore.model.LoyaltyTransaction;
import com.api.rest.conveniencestore.repository.ClientRepository;
import com.api.rest.conveniencestore.repository.LoyaltyPointRepository;
import com.api.rest.conveniencestore.repository.LoyaltyTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoyaltyService {

    private static final int POINTS_PER_REAL = 1;
    private static final int POINTS_PER_DISCOUNT_UNIT = 100;
    private static final double DISCOUNT_PER_UNIT = 1.0;
    private static final double MAX_DISCOUNT_PERCENTAGE = 0.5;
    private static final int MIN_POINTS_TO_REDEEM = 100;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoyaltyPointRepository loyaltyPointRepository;

    @Autowired
    private LoyaltyTransactionRepository loyaltyTransactionRepository;

    @Transactional
    public void earnPoints(Client client, double totalValue, Long saleId) {
        int points = (int) Math.floor(totalValue) * POINTS_PER_REAL;
        if (points <= 0) return;

        client.addPoints(points);
        clientRepository.save(client);

        loyaltyPointRepository.save(new LoyaltyPoint(client.getId(), points));
        loyaltyTransactionRepository.save(new LoyaltyTransaction(client.getId(), points, TransactionType.EARN, saleId));
    }

    @Transactional
    public double redeemPoints(Client client, int pointsToUse, double purchaseAmount, Long saleId) {
        if (pointsToUse < MIN_POINTS_TO_REDEEM) {
            throw new LoyaltyException("Mínimo de " + MIN_POINTS_TO_REDEEM + " pontos para resgate.");
        }
        if (pointsToUse % POINTS_PER_DISCOUNT_UNIT != 0) {
            throw new LoyaltyException("Pontos para resgate devem ser múltiplos de " + POINTS_PER_DISCOUNT_UNIT + ".");
        }

        double maxDiscount = purchaseAmount * MAX_DISCOUNT_PERCENTAGE;
        double requestedDiscount = (pointsToUse / POINTS_PER_DISCOUNT_UNIT) * DISCOUNT_PER_UNIT;
        double discount = Math.min(requestedDiscount, maxDiscount);
        int effectivePoints = (int) (discount / DISCOUNT_PER_UNIT) * POINTS_PER_DISCOUNT_UNIT;

        if (client.getPointsBalance() < effectivePoints) {
            throw new LoyaltyException("Saldo insuficiente. Saldo atual: " + client.getPointsBalance() + " pontos.");
        }

        // consome lotes mais antigos primeiro (FIFO)
        int remaining = effectivePoints;
        List<LoyaltyPoint> batches = loyaltyPointRepository
                .findByClientIdAndRemainingPointsGreaterThanOrderByCreatedAtAsc(client.getId(), 0);
        for (LoyaltyPoint batch : batches) {
            if (remaining <= 0) break;
            int toDeduct = Math.min(batch.getRemainingPoints(), remaining);
            batch.deductPoints(toDeduct);
            loyaltyPointRepository.save(batch);
            remaining -= toDeduct;
        }

        client.deductPoints(effectivePoints);
        clientRepository.save(client);

        loyaltyTransactionRepository.save(new LoyaltyTransaction(client.getId(), -effectivePoints, TransactionType.REDEEM, saleId));

        return discount;
    }

    @Transactional
    public void cancelPoints(Long saleId) {
        List<LoyaltyTransaction> transactions = loyaltyTransactionRepository.findByReferenceId(saleId);
        for (LoyaltyTransaction tx : transactions) {
            Long clientId = tx.getClientId();
            if (clientId == null) continue;
            clientRepository.findById(clientId).ifPresent(client -> {
                // estorna o efeito original (EARN positivo → remove; REDEEM negativo → devolve)
                client.deductPoints(tx.getPoints());
                clientRepository.save(client);
                loyaltyTransactionRepository.save(
                        new LoyaltyTransaction(client.getId(), -tx.getPoints(), TransactionType.CANCEL, saleId));
            });
        }
    }

    public LoyaltySimulateResponseDto simulate(Client client, double purchaseAmount, int pointsToUse) {
        if (pointsToUse < MIN_POINTS_TO_REDEEM) {
            throw new LoyaltyException("Mínimo de " + MIN_POINTS_TO_REDEEM + " pontos para resgate.");
        }
        if (pointsToUse % POINTS_PER_DISCOUNT_UNIT != 0) {
            throw new LoyaltyException("Pontos para resgate devem ser múltiplos de " + POINTS_PER_DISCOUNT_UNIT + ".");
        }
        if (client.getPointsBalance() < pointsToUse) {
            throw new LoyaltyException("Saldo insuficiente. Saldo atual: " + client.getPointsBalance() + " pontos.");
        }

        double maxDiscount = purchaseAmount * MAX_DISCOUNT_PERCENTAGE;
        double requestedDiscount = (pointsToUse / POINTS_PER_DISCOUNT_UNIT) * DISCOUNT_PER_UNIT;
        double discount = Math.min(requestedDiscount, maxDiscount);
        int effectivePoints = (int) (discount / DISCOUNT_PER_UNIT) * POINTS_PER_DISCOUNT_UNIT;

        double finalAmount = purchaseAmount - discount;
        int pointsEarned = (int) Math.floor(finalAmount) * POINTS_PER_REAL;
        int pointsAfterPurchase = client.getPointsBalance() - effectivePoints + pointsEarned;

        return new LoyaltySimulateResponseDto(effectivePoints, discount, finalAmount, pointsAfterPurchase);
    }

    public Page<LoyaltyTransactionDto> getTransactions(Long clientId, Pageable pageable) {
        return loyaltyTransactionRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
                .map(LoyaltyTransactionDto::new);
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void expirePoints() {
        List<LoyaltyPoint> expired = loyaltyPointRepository
                .findByExpirationDateBeforeAndRemainingPointsGreaterThan(LocalDateTime.now(), 0);
        for (LoyaltyPoint batch : expired) {
            int points = batch.getRemainingPoints();
            Long clientId = batch.getClientId();
            if (clientId == null) continue;
            clientRepository.findById(clientId).ifPresent(client -> {
                client.deductPoints(points);
                clientRepository.save(client);
                loyaltyTransactionRepository.save(
                        new LoyaltyTransaction(client.getId(), -points, TransactionType.EXPIRE, null));
            });
            batch.expirePoints();
            loyaltyPointRepository.save(batch);
        }
    }
}
