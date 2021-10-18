package at.fhv.se.banking.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.repositories.AccountRepository;

@Component
public class HibernateAccountRepository implements AccountRepository {

    private List<Account> accounts;

    HibernateAccountRepository() {
        this.accounts = new ArrayList<>();
    }
    
    @Override
    public List<Account> forCustomer(CustomerId customerId) {
        return this.accounts.stream().filter(a -> a.owner().equals(customerId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Account> byIban(Iban iban) {
        return this.accounts.stream().filter(a -> a.iban().equals(iban)).findFirst();
    }

    @Override
    public void add(Account a) {
        this.accounts.add(a);
    }
}
