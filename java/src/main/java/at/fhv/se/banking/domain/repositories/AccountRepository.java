package at.fhv.se.banking.domain.repositories;

import java.util.List;
import java.util.Optional;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;

public interface AccountRepository {

    List<Account> forCustomer(CustomerId customerId);

    Optional<Account> byIban(Iban iban);
}
