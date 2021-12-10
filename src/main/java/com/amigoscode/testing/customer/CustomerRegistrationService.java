package com.amigoscode.testing.customer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {


    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) throws Exception {
        Optional<Customer> optionalCustomer = this.customerRepository.selectCustomerByPhoneNumber(request.getCustomer().getPhoneNumber());
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
