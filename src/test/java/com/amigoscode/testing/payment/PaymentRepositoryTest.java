package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;



@DataJpaTest(
        properties = "spring.jpa.properties.javax.persistence.validation.mode=none"
)
class PaymentRepositoryTest {
    
    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        //Given
        Payment payment = new Payment(1L,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "Donation");
        //When
        underTest.save(payment);

        //Then
        Optional<Payment> optionalPayment = underTest.findById(1L);
        assertThat(optionalPayment).isPresent().hasValueSatisfying(payment1 -> assertThat(payment1).isEqualToComparingFieldByField(payment));
    }
}