package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository; // = mock(CustomerRepository.class);

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.underTest = new CustomerRegistrationService(this.customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() throws Exception {
        //Given a phone number and a request
        String phoneNumber = "003399";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        // ,,,,,, a request ,,,,,,
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(customerRegistrationRequest);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); //result from save method
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() throws Exception {
        //Given a phone number and a request
        String phoneNumber = "003399";
        Customer customer = new Customer(null, "Mariam", phoneNumber);

        // ,,,,,, a request ,,,,,,
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(customerRegistrationRequest);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); //result from save method
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue)
                .isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue).isNotNull();
    }

    @Test
    void itShouldNotSaveWhenCustomerExists() throws Exception {
        //Given a phone number and a request
        String phoneNumber = "003399";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        // ,,,,,, a request ,,,,,,
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // a customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //When
        underTest.registerNewCustomer(customerRegistrationRequest);

        //Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotTakeoverPhoneNumber() {
        //Given a phone number and a request
        String phoneNumber = "003399";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);
        Customer customer1 = new Customer(UUID.randomUUID(), "Not_Mariam", phoneNumber);

        // ,,,,,, a request ,,,,,,
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer1);

        // a customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));


        //When
        //Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number %s is taken !", phoneNumber));
        then(customerRepository).should(never()).save(any(Customer.class));
    }

}