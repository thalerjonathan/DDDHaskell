package at.fhv.se.banking.application.api;

import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.domain.events.TransferFailed;
import at.fhv.se.banking.domain.events.TransferSent;

public interface AccountService {
    
    AccountDTO accountByIban(String iban) throws AccountNotFoundException;
    
    void deposit(double amount, String iban) throws AccountNotFoundException, InvalidOperationException;
    void withdraw(double amount, String iban) throws AccountNotFoundException, InvalidOperationException;

    // this is the transactional (strong) consistent transfer
    void transferTransactional(double amount, String reference, String sendingIban, String receivingIban) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException;
    // this is the eventual consistent transfer
    void transferEventual(double amount, String reference, String sendingIban, String receivingIban) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException;
    
    // to be called from async processing backend when processing the Domain Event
    void transferReceive(TransferSent transferSent);
    void transferFailed(TransferFailed transferFailed);
}
