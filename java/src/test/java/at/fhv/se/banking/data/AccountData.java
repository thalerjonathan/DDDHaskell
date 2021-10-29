package at.fhv.se.banking.data;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.SavingsAccount;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public class AccountData {
    
    public static Account createGiro() {
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        return new GiroAccount(cId, iban);
    }

    public static Account createSavings() {
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        return new SavingsAccount(cId, iban);
    }

    public static Account createGiroWithBalance(double balance) {
        Account a = createGiro();

        try {
            // should not happen
            a.deposit(balance, LocalDateTime.now());
        } catch (AccountException e) {
            return null;
        }

        return a;
    }

    public static Account createSavingsWithBalance(double balance) {
        Account a = createSavings();
        a.receiveFrom(a.iban(), balance, "Deposit", "Deposit", LocalDateTime.now());
        return a;
    }
}
