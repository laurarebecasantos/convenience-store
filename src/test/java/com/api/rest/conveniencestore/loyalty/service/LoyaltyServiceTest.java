package com.api.rest.conveniencestore.loyalty.service;

import com.api.rest.conveniencestore.client.dto.ClientDto;
import com.api.rest.conveniencestore.loyalty.dto.LoyaltySimulateResponseDto;
import com.api.rest.conveniencestore.loyalty.dto.LoyaltyTransactionDto;
import com.api.rest.conveniencestore.shared.enums.TransactionType;
import com.api.rest.conveniencestore.shared.exception.LoyaltyException;
import com.api.rest.conveniencestore.client.model.Client;
import com.api.rest.conveniencestore.loyalty.model.LoyaltyPoint;
import com.api.rest.conveniencestore.loyalty.model.LoyaltyTransaction;
import com.api.rest.conveniencestore.client.repository.ClientRepository;
import com.api.rest.conveniencestore.loyalty.repository.LoyaltyPointRepository;
import com.api.rest.conveniencestore.loyalty.repository.LoyaltyTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LoyaltyPointRepository loyaltyPointRepository;

    @Mock
    private LoyaltyTransactionRepository loyaltyTransactionRepository;

    @InjectMocks
    private LoyaltyService loyaltyService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(new ClientDto("Maria Silva", "123.456.789-09"));
    }

    // === earnPoints ===

    @Test
    void earnPoints_ShouldAddPointsAndSaveBatchAndTransaction() {
        loyaltyService.earnPoints(client, 150.99, 1L);

        assertThat(client.getPointsBalance()).isEqualTo(150); // floor(150.99)
        verify(clientRepository).save(client);
        verify(loyaltyPointRepository).save(any(LoyaltyPoint.class));
        verify(loyaltyTransactionRepository).save(any(LoyaltyTransaction.class));
    }

    @Test
    void earnPoints_WhenValueIsZero_ShouldNotAddPoints() {
        loyaltyService.earnPoints(client, 0.50, 1L);

        assertThat(client.getPointsBalance()).isEqualTo(0);
        verify(clientRepository, never()).save(any());
    }

    // === redeemPoints ===

    @Test
    void redeemPoints_ShouldDeductFIFOAndReturnDiscount() {
        client.addPoints(500);
        LoyaltyPoint batch = new LoyaltyPoint(1L, 500);

        when(loyaltyPointRepository.findByClientIdAndRemainingPointsGreaterThanOrderByCreatedAtAsc(any(), eq(0)))
                .thenReturn(List.of(batch));

        double discount = loyaltyService.redeemPoints(client, 200, 100.0, 1L);

        assertThat(discount).isEqualTo(2.0); // 200pts / 100 = R$2
        assertThat(client.getPointsBalance()).isEqualTo(300); // 500 - 200
        assertThat(batch.getRemainingPoints()).isEqualTo(300);
        verify(loyaltyTransactionRepository).save(any(LoyaltyTransaction.class));
    }

    @Test
    void redeemPoints_ShouldRespectMax50Percent() {
        client.addPoints(10000);
        LoyaltyPoint batch = new LoyaltyPoint(1L, 10000);

        when(loyaltyPointRepository.findByClientIdAndRemainingPointsGreaterThanOrderByCreatedAtAsc(any(), eq(0)))
                .thenReturn(List.of(batch));

        double discount = loyaltyService.redeemPoints(client, 10000, 10.0, 1L);

        assertThat(discount).isEqualTo(5.0); // max 50% de R$10
        assertThat(client.getPointsBalance()).isEqualTo(9500); // 10000 - 500pts efetivos
    }

    @Test
    void redeemPoints_WhenBelowMinimum_ShouldThrow() {
        client.addPoints(50);

        assertThatThrownBy(() -> loyaltyService.redeemPoints(client, 50, 100.0, 1L))
                .isInstanceOf(LoyaltyException.class)
                .hasMessageContaining("100");
    }

    @Test
    void redeemPoints_WhenNotMultipleOf100_ShouldThrow() {
        client.addPoints(500);

        assertThatThrownBy(() -> loyaltyService.redeemPoints(client, 150, 100.0, 1L))
                .isInstanceOf(LoyaltyException.class)
                .hasMessageContaining("múltiplos");
    }

    @Test
    void redeemPoints_WhenInsufficientBalance_ShouldThrow() {
        client.addPoints(100);

        assertThatThrownBy(() -> loyaltyService.redeemPoints(client, 200, 100.0, 1L))
                .isInstanceOf(LoyaltyException.class)
                .hasMessageContaining("insuficiente");
    }

    // === cancelPoints ===

    @Test
    void cancelPoints_ShouldReverseEarnTransaction() {
        client.addPoints(100);
        LoyaltyTransaction earnTx = new LoyaltyTransaction(1L, 100, TransactionType.EARN, 1L);

        when(loyaltyTransactionRepository.findByReferenceId(1L)).thenReturn(List.of(earnTx));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        loyaltyService.cancelPoints(1L);

        assertThat(client.getPointsBalance()).isEqualTo(0); // 100 - 100
        verify(loyaltyTransactionRepository).save(any(LoyaltyTransaction.class));
    }

    @Test
    void cancelPoints_ShouldAllowNegativeBalance() {
        // cliente ganhou 100 e já usou tudo, saldo = 0
        LoyaltyTransaction earnTx = new LoyaltyTransaction(1L, 100, TransactionType.EARN, 1L);

        when(loyaltyTransactionRepository.findByReferenceId(1L)).thenReturn(List.of(earnTx));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        loyaltyService.cancelPoints(1L);

        assertThat(client.getPointsBalance()).isEqualTo(-100); // 0 - 100
    }

    // === simulate ===

    @Test
    void simulate_ShouldReturnCorrectProjection() {
        client.addPoints(1000);

        LoyaltySimulateResponseDto result = loyaltyService.simulate(client, 200.0, 500);

        assertThat(result.discount()).isEqualTo(5.0); // 500/100 = R$5
        assertThat(result.finalAmount()).isEqualTo(195.0);
        assertThat(result.pointsToUse()).isEqualTo(500);
        assertThat(result.pointsAfterPurchase()).isEqualTo(695); // 1000 - 500 + 195
    }

    @Test
    void simulate_WhenInsufficientBalance_ShouldThrow() {
        client.addPoints(100);

        assertThatThrownBy(() -> loyaltyService.simulate(client, 200.0, 500))
                .isInstanceOf(LoyaltyException.class);
    }

    // === getTransactions ===

    @Test
    void getTransactions_ShouldReturnMappedDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        LoyaltyTransaction tx = new LoyaltyTransaction(1L, 100, TransactionType.EARN, 1L);
        when(loyaltyTransactionRepository.findByClientIdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(tx)));

        Page<LoyaltyTransactionDto> result = loyaltyService.getTransactions(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).type()).isEqualTo(TransactionType.EARN);
    }

    // === expirePoints ===

    @Test
    void expirePoints_ShouldZeroBatchAndDeductFromClient() {
        client.addPoints(200);
        LoyaltyPoint expiredBatch = new LoyaltyPoint(1L, 200);

        when(loyaltyPointRepository.findByExpirationDateBeforeAndRemainingPointsGreaterThan(any(LocalDateTime.class), eq(0)))
                .thenReturn(List.of(expiredBatch));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        loyaltyService.expirePoints();

        assertThat(client.getPointsBalance()).isEqualTo(0); // 200 - 200
        assertThat(expiredBatch.getRemainingPoints()).isEqualTo(0);
        verify(loyaltyTransactionRepository).save(any(LoyaltyTransaction.class));
    }
}
