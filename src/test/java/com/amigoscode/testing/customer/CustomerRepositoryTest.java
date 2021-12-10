package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;


@DataJpaTest(
        properties = "spring.jpa.properties.javax.persistence.validation.mode=none"
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given
        //When
        //Then
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID randomUUID = UUID.randomUUID();
        Customer customer = new Customer(randomUUID, "Adel", "0000");

        //When
        underTest.save(customer);

        //Then
        Optional<Customer> optionalCustomer = underTest.findById(randomUUID);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    //assertThat(c.getId()).isEqualTo(randomUUID);
                    //assertThat(c.getName()).isEqualTo("Adel");
                    //assertThat(c.getPhoneNumber()).isEqualTo("0000");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });

    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "00000");

        //When
        //Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value")
                        .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Sara", null);

        //When
        //Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value")
                        .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldSelectCustomerByThePhoneNumber() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "656565";
        Customer customer = new Customer(id, "Sara", phoneNumber);
        underTest.save(customer);

        //When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        //Then
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(customer));

        //when number does not exist
        Optional<Customer> optionalCustomer1 = underTest.selectCustomerByPhoneNumber("46456745756756765");
        assertThat(optionalCustomer1).isNotPresent();
    }
}