package at.fhv.se.banking.infrastructure.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.infrastructure.db.utils.Utils;

@Component
public class HibernateAccountRepository implements AccountRepository {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<Account> forCustomer(CustomerId customerId) {
        TypedQuery<Account> q = this.em.createQuery("FROM Account a WHERE a.owner = :cId", Account.class);
        q.setParameter("cId", customerId);

        return q.getResultList();
    }

    @Override
    public Optional<Account> byIban(Iban iban) {
        TypedQuery<Account> q = this.em.createQuery("FROM Account a WHERE a.iban = :iban", Account.class);
        q.setParameter("iban", iban);

        return Utils.getOptionalResult(q);
    }

    @Override
    public void add(Account a) {
        this.em.persist(a);
    }
}
