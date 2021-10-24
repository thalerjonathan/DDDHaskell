package at.fhv.se.banking.domain.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.repositories.AccountRepository;

public class InMemoryAccountRepository implements AccountRepository {

    private List<Account> accounts;

    InMemoryAccountRepository() {
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