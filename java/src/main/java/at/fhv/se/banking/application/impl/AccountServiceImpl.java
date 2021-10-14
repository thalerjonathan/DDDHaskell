package at.fhv.se.banking.application.impl;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.dto.AccountDTO;

@Component
public class AccountServiceImpl implements AccountService {

    @Override
    public AccountDTO accountByIban(String iban) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deposit(String iban, double amount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void withdraw(String iban, double amount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void transfer(String fromIban, String receivingIban, double amount, String reference) {
        // TODO Auto-generated method stub
        
    }
    
}
