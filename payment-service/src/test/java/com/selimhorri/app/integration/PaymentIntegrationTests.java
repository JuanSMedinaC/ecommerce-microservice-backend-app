package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PaymentServiceIntegrationTests {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .orderId(100)
                .isPayed(false)
                .build();
        payment = paymentRepository.save(payment);
    }

    @Test
    void savePayment_PersistsAndRetrievesSuccessfully() {
        Optional<Payment> found = paymentRepository.findById(payment.getPaymentId());
        assertThat(found).isPresent();
        assertThat(found.get().getOrderId()).isEqualTo(100);
        assertThat(found.get().getIsPayed()).isFalse();
    }

    @Test
    void updateIsPayed_ChangesStatusCorrectly() {
        payment.setIsPayed(true);
        paymentRepository.save(payment);

        Optional<Payment> updated = paymentRepository.findById(payment.getPaymentId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getIsPayed()).isTrue();
    }
}