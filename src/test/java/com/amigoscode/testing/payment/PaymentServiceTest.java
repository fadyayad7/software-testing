package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() throws Exception {
        //Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                1L,
                null,
                new BigDecimal("100.00"),
                Currency.USD,
                "card123xxxxx",
                "Business"
        ));

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCustomer(customerId, paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId", "paymentId");
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);

    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() throws Exception {
        //Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                1L,
                null,
                new BigDecimal("100.00"),
                Currency.USD,
                "card123xxxxx",
                "Business"
        ));

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        //When
        assertThatThrownBy(() -> underTest.chargeCustomer(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId));

        //Then
        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void itShouldNotChargeWhenCurrencyIsNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                1L,
                null,
                new BigDecimal("100.00"),
                Currency.EUR,
                "card123xxxxx",
                "Business"
        ));


        //When
        assertThatThrownBy(() -> underTest.chargeCustomer(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] not supported", Currency.EUR));

        //Then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeWhenCustomerIsNotPresent() {
        //Given
        UUID customerId = UUID.randomUUID();
        //When
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                1L,
                null,
                new BigDecimal("100.00"),
                Currency.EUR,
                "card123xxxxx",
                "Business"
        ));


        //When
        assertThatThrownBy(() -> underTest.chargeCustomer(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer [%s] is not found", customerId));
        //Then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

}