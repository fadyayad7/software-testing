package com.amigoscode.testing.customer;


import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {


    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository, PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();
        if (!phoneNumberValidator.test(phoneNumber))
            throw new IllegalStateException(String.format("Phone number %s is not valid", phoneNumber));

        Optional<Customer> optionalCustomer = this.customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            if (customer.getName().equals(request.getCustomer().getName()))
                return;
            else
                throw new IllegalStateException(String.format("phone number %s is taken !", customer.getPhoneNumber()));
        }

        if (request.getCustomer().getId() == null)
            request.getCustomer().setId(UUID.randomUUID());

        this.customerRepository.save(request.getCustomer());
    }
}
