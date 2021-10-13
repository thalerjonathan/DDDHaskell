package at.fhv.se.banking.domain.repositories;

import java.util.List;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.CustomerId;

public interface AccountRepository {

    List<Account> forCustomer(CustomerId customerId);
    
}
