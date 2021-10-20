package at.fhv.se.banking.application.api;

import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.application.dto.AccountDTO;

public interface AccountService {
    
    AccountDTO accountByIban(String iban) throws AccountNotFoundException;
    void deposit(String iban, double amount) throws AccountNotFoundException, InvalidOperationException;
    void withdraw(String iban, double amount) throws AccountNotFoundException, InvalidOperationException;
    void transfer(String sendingIban, String receivingIban, double amount, String reference) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException;

}
