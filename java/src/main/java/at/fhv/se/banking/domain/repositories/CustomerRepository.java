package at.fhv.se.banking.domain.repositories;

import java.util.Optional;

import at.fhv.se.banking.domain.model.Customer;

public interface CustomerRepository {

    Optional<Customer> byName(String customerName);
    
}
