package at.fhv.se.banking.application.api;

import at.fhv.se.banking.application.dto.AccountDTO;

public interface AccountService {
    
    AccountDTO accountByIban(String iban);

}
