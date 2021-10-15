package at.fhv.se.banking.infrastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class HibernateCustomerRepository implements CustomerRepository {

    private List<Customer> customers;

    HibernateCustomerRepository() {
        this.customers = new ArrayList<>();
    }

    @Override
    public Optional<Customer> byId(CustomerId customerId) {
        return this.customers.stream().filter(c -> c.customerId().equals(customerId)).findFirst();
    }

    @Override
    public List<Customer> all() {
        return Collections.unmodifiableList(this.customers);
    }

    @Override
    public void add(Customer c) {
        this.customers.add(c);
    }

    @Override
    public CustomerId nextCustomerId() {
        return new CustomerId(UUID.randomUUID().toString().toLowerCase());
    }
}
