package at.fhv.se.banking.infrastructure.db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class HibernateCustomerRepository implements CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Customer> byId(CustomerId cId) {
        TypedQuery<Customer> q = this.em.createQuery("FROM Customer c WHERE c.customerId = :cId", Customer.class);
        q.setParameter("cId", cId);

        return Utils.getOptionalResult(q);
    }

    @Override
    public List<Customer> all() {
        TypedQuery<Customer> q = this.em.createQuery("FROM Customer c", Customer.class);
        return q.getResultList();
    }

    @Override
    public void add(Customer c) {
        this.em.persist(c);
    }

    @Override
    public CustomerId nextCustomerId() {
        return new CustomerId(UUID.randomUUID().toString().toLowerCase());
    }
}
