package at.fhv.se.banking.domain.services.api;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public interface TransferService {

    void transfer(double amount, String reference, LocalDateTime now, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException;

    void transferSend(double amount, String reference, LocalDateTime now, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException;
    
    void transferReceive(double amount, String reference, LocalDateTime now, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException;
}
