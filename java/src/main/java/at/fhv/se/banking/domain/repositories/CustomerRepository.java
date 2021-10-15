package at.fhv.se.banking.domain.repositories;

import java.util.List;
import java.util.Optional;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;

public interface CustomerRepository {

    void add(Customer customer);
    CustomerId nextCustomerId();
    Optional<Customer> byId(CustomerId customerId);
    List<Customer> all();
}
