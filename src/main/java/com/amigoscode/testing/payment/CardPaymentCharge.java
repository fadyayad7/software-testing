package com.amigoscode.testing.payment;

import lombok.Getter;
import lombok.ToString;

@Getter
public class CardPaymentCharge {

    private final boolean isCardDebited;

    public CardPaymentCharge(boolean isCardDebited) {
        this.isCardDebited = isCardDebited;
    }
}
