package at.fhv.se.banking.application.api;

import java.util.List;

import at.fhv.se.banking.domain.model.Account;

public interface BankingService {

    List<Account> listAccountsForCustomer(String customer);
    
}
