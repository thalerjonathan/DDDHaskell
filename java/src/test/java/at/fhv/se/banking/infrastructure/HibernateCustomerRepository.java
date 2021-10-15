package at.fhv.se.banking.infrastructure;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class HibernateCustomerRepository implements CustomerRepository {

    @Override
    public Optional<Customer> byId(CustomerId customerId) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public List<Customer> all() {
        // TODO: implement
        return Collections.emptyList();
    }

    @Override
    public Optional<Customer> byIban(Iban iban) {
        // TODO: implement
        return Optional.empty();
    }
}
