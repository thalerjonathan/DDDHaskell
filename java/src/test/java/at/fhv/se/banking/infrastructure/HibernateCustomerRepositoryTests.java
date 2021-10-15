package at.fhv.se.banking.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;

public class HibernateCustomerRepositoryTests {
    
    private HibernateCustomerRepository repo;

    @BeforeEach
    public void beforeEach() {
        this.repo = new HibernateCustomerRepository();
    }

    @Test
    public void given_customer_when_added_then_fetched() {
        // given
        CustomerId cId = repo.nextCustomerId();
        Customer customer = new Customer(cId, "Jonathan Thaler");

        // when
        repo.add(customer);

        // then
        Optional<Customer> actualCustomer = repo.byId(cId);
        compareCustomers(customer, actualCustomer.get());
    }

    @Test
    public void given_customers_when_addedandall_then_fetched() {
        // given
        CustomerId cId1 = repo.nextCustomerId();
        CustomerId cId2 = repo.nextCustomerId();
        Customer customer1 = new Customer(cId1, "Jonathan Thaler");
        Customer customer2 = new Customer(cId2, "Thomas Schwarz");

        // when
        repo.add(customer1);
        repo.add(customer2);

        // then
        List<Customer> allCustomers = repo.all();
        assertEquals(2, allCustomers.size());
        compareCustomers(customer1, allCustomers.get(0));
        compareCustomers(customer2, allCustomers.get(1));
    }

    private static void compareCustomers(Customer expected, Customer actual) {
        assertEquals(expected.customerId(), actual.customerId());
        assertEquals(expected.name(), actual.name());
        
    }
}
