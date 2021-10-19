package at.fhv.se.banking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.model.TXLine;

public class AccountTests {
    
    @Test
    public void given_params_when_newaccount_then_reflectsparameters_balancezero() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;

        // when
        Account a = new Account(cId, iban, type);

        // then
        assertEquals(cId, a.owner());
        assertEquals(iban, a.iban());
        assertEquals(0, a.balance());
        assertEquals(type, a.type());
        assertTrue(a.transactions().isEmpty());
    }

    @Test
    public void given_newaccount_when_deposit_then_reflectsbalance() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;
        Account a = new Account(cId, iban, type);
        double depositAmount = 1234;

        // TODO: check for TXLines
        //TXLine tx = new TXLine();

        // when
        a.deposit(depositAmount);

        // then
        assertEquals(depositAmount, a.balance());
    }

    @Test
    public void given_newaccount_when_withdraw_then_reflectsnegativebalance() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;
        Account a = new Account(cId, iban, type);
        double withdrawAmount = 123;

        // TODO: check for TXLines
        //TXLine tx = new TXLine();

        // when
        a.withdraw(withdrawAmount);

        // then
        assertEquals(-withdrawAmount, a.balance());
    }

    @Test
    public void given_newaccount_when_withdrawafterdeposit_then_reflectsbalance() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;
        Account a = new Account(cId, iban, type);
        double depositAmount = 1234;
        double withdrawAmount = 1000;

        // TODO: check for TXLines
        //TXLine tx = new TXLine();

        // when
        a.deposit(depositAmount);
        a.withdraw(withdrawAmount);

        // then
        assertEquals(depositAmount - withdrawAmount, a.balance());
    }

    @Test
    public void given_newaccount_when_transferto_then_reflectsbalance() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;
        Account a = new Account(cId, iban, type);

        double amount = 123;
        String name = "Max Mustermann";
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();
        Iban toIban = new Iban("AT98 76543 09876543210");
        TXLine tx = new TXLine(toIban, -amount, name, reference, now);

        // when
        a.transferTo(toIban, amount, name, reference, now);

        // then
        assertEquals(-amount, a.balance());
        assertEquals(1, a.transactions().size());
        assertEquals(tx, a.transactions().get(0));
    }

    @Test
    public void given_newaccount_when_receiveFrom_then_reflectsbalance() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");
        AccountType type = AccountType.GIRO;
        Account a = new Account(cId, iban, type);

        double amount = 123;
        String name = "Max Mustermann";
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();
        Iban fromIban = new Iban("AT98 76543 09876543210");
        TXLine tx = new TXLine(fromIban, amount, name, reference, now);

        // when
        a.receiveFrom(fromIban, amount, name, reference, now);

        // then
        assertEquals(amount, a.balance());
        assertEquals(1, a.transactions().size());
        assertEquals(tx, a.transactions().get(0));
    }
}
