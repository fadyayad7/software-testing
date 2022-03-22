package com.amigoscode.testing.payment;


import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }


    public void chargeCustomer(UUID customerId, PaymentRequest paymentRequest) throws Exception {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent())
            throw new IllegalStateException(String.format("Customer [%s] is not found", customerId));

        if (!List.of(Currency.USD, Currency.GBP).contains(paymentRequest.getPayment().getCurrency()))
            throw new IllegalStateException(String.format("Currency [%s] not supported", paymentRequest.getPayment().getCurrency()));
//        if (!(paymentRequest.getPayment().getCurrency() instanceof Currency))
//            throw new Exception();

        //charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );
        if (!cardPaymentCharge.isCardDebited()) throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));

        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());

        //TODO : send sms
    }
}
