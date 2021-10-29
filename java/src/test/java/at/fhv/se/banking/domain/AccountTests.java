package at.fhv.se.banking.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import at.fhv.se.banking.data.AccountData;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.TXLine;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public class AccountTests {
    
    @Test
    public void given_params_when_newaccount_then_reflectsparameters_balancezero() {
        // given
        CustomerId cId = new CustomerId("1");
        Iban iban = new Iban("AT12 12345 01234567890");

        // when
        Account a = new GiroAccount(cId, iban);

        // then
        assertEquals(cId, a.owner());
        assertEquals(iban, a.iban());
        assertEquals(0, a.balance(), 0.001);
        assertEquals("GIRO", a.type());
        assertTrue(a.transactions().isEmpty());
    }

    @Test
    public void given_newaccount_when_deposit_then_reflectsbalance() throws AccountException {
        // given
        Account a = AccountData.createGiro();
        LocalDateTime now = LocalDateTime.now();

        double depositAmount = 1234;
    
        List<TXLine> tx = Arrays.asList(
            new TXLine(a, a.iban(), depositAmount, "Deposit", "Deposit", now)
        );

        // when
        a.deposit(depositAmount, now);

        // then
        assertEquals(depositAmount, a.balance(), 0.001);
        assertEquals(tx, a.transactions());
    }

    @Test
    public void given_newaccount_when_withdraw_then_reflectsnegativebalance() throws AccountException {
        // given
        Account a = AccountData.createGiro();
        LocalDateTime now = LocalDateTime.now();

        double withdrawAmount = 123;

        List<TXLine> tx = Arrays.asList(
            new TXLine(a, a.iban(), -withdrawAmount, "Withdraw", "Withdraw", now)
        );

        // when
        a.withdraw(withdrawAmount, now);

        // then
        assertEquals(-withdrawAmount, a.balance(), 0.001);
        assertEquals(tx, a.transactions());
    }

    @Test
    public void given_newaccount_when_withdrawafterdeposit_then_reflectsbalance() throws AccountException {
        // given
        Account a = AccountData.createGiro();
        LocalDateTime now = LocalDateTime.now();

        double depositAmount = 1234;
        double withdrawAmount = 1000;

        List<TXLine> tx = Arrays.asList(
            new TXLine(a, a.iban(), depositAmount, "Deposit", "Deposit", now),
            new TXLine(a, a.iban(), -withdrawAmount, "Withdraw", "Withdraw", now)
        );

        // when
        a.deposit(depositAmount, now);
        a.withdraw(withdrawAmount, now);

        // then
        assertEquals(depositAmount - withdrawAmount, a.balance(), 0.001);
        assertEquals(tx, a.transactions());
    }

    @Test
    public void given_newaccount_when_transferto_then_reflectsbalance() throws AccountException {
        // given
        Account a = AccountData.createGiro();

        double amount = 123;
        String name = "Max Mustermann";
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();
        Iban toIban = new Iban("AT98 76543 09876543210");
        TXLine tx = new TXLine(a, toIban, -amount, name, reference, now);

        // when
        a.transferTo(toIban, amount, name, reference, now);

        // then
        assertEquals(-amount, a.balance(), 0.001);
        assertEquals(1, a.transactions().size());
        assertEquals(tx, a.transactions().iterator().next());
    }

    @Test
    public void given_newaccount_when_receiveFrom_then_reflectsbalance() {
        // given
        Account a = AccountData.createGiro();

        double amount = 123;
        String name = "Max Mustermann";
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();
        Iban fromIban = new Iban("AT98 76543 09876543210");
        TXLine tx = new TXLine(a, fromIban, amount, name, reference, now);

        // when
        a.receiveFrom(fromIban, amount, name, reference, now);

        // then
        assertEquals(amount, a.balance(), 0.001);
        assertEquals(1, a.transactions().size());
        assertEquals(tx, a.transactions().iterator().next());
    }

    @Test
    public void given_giroaccount_when_overdraftover1000_then_exception() {
        // given
        Account a = AccountData.createGiro();
        double overdraftLimit = 1000;
        double withdrawAmount = overdraftLimit + 1;

        // when
        assertThrows(AccountException.class, () -> a.withdraw(withdrawAmount, LocalDateTime.now()));
    }

    @Test
    public void given_savingsaccountwithbalance_when_overdraftthroughtransfer_then_exception() {
        // given
        Iban sendingIban = new Iban("AT12 12345 23456789012");
        Account a = AccountData.createSavings();
        a.receiveFrom(sendingIban, 1000, "Savings Deposit", "Savings Deposit", LocalDateTime.now());
        
        double withdrawAmount = 1001;

        // when
        assertThrows(AccountException.class, () -> a.transferTo(sendingIban, withdrawAmount, "Savings Withdraw", "Savings Withdraw", LocalDateTime.now()));
    }

    @Test
    public void given_savingsaccountwithbalance_when_withdraw_then_exception() {
        // given
        Account a = AccountData.createSavings();
        a.receiveFrom(a.iban(), 1000, "Savings Transfer", "Savings Transfer", LocalDateTime.now());

        // when
        assertThrows(AccountException.class, () -> a.withdraw(100, LocalDateTime.now()));
    }

    @Test
    public void given_savingsaccount_when_deposit_then_exception() {
        // given
        Account a = AccountData.createSavings();
        
        // when
        assertThrows(AccountException.class, () -> a.deposit(100, LocalDateTime.now()));
    }
}
