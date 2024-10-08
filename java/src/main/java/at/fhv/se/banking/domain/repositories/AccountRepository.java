package at.fhv.se.banking.domain.repositories;

import java.util.List;
import java.util.Optional;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.Iban;

public interface AccountRepository {

    void add(Account account);
    List<Account> forCustomer(CustomerId customerId);
    Optional<Account> byIban(Iban iban);
}
