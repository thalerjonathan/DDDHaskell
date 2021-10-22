package at.fhv.se.banking.domain.services.api;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public interface TransferService {

    void transfer(double transferAmount, String reference, LocalDateTime now, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException;
    
}
