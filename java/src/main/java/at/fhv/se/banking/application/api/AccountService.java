package at.fhv.se.banking.application.api;

import at.fhv.se.banking.application.dto.AccountDTO;

public interface AccountService {
    
    AccountDTO accountByIban(String iban);
    
    void deposit(String iban, double amount);
    void withdraw(String iban, double amount);
    
    void transfer(String fromIban, String receivingIban, double amount, String reference);

}
