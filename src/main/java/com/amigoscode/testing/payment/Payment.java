package com.amigoscode.testing.payment;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@ToString
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private long paymentId;

    private UUID customerId;

    private BigDecimal amount;

    private Currency currency;

    private String source;

    private String Description;
}
