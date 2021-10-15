package at.fhv.se.banking.domain.repositories;

import java.util.List;
import java.util.Optional;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;

public interface CustomerRepository {

    Optional<Customer> byId(CustomerId customerId);
    
    List<Customer> all();

    Optional<Customer> byIban(Iban iban);
    
}
