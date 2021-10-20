package at.fhv.se.banking.domain;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.SavingsAccount;

public class AccountData {
    
    public static Account createGiro() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        return new GiroAccount(cId, iban);
    }

    public static Account createSavings() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        return new SavingsAccount(cId, iban);
    }
}
