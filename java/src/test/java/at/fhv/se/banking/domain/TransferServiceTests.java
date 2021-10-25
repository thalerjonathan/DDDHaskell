package at.fhv.se.banking.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.SavingsAccount;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.services.api.TransferService;
import at.fhv.se.banking.domain.services.impl.TransferServiceImpl;

public class TransferServiceTests {
    
    // NOTE: not using dependency injection, want to keep tests fast
    private TransferService transferService;

    @BeforeEach
    public void beforeEach() {
        this.transferService = new TransferServiceImpl();
    }

    @Test
    public void given_customers_and_accounts_when_transfer_then_exchangebalance() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(receivingCustomer.customerId(), receivingIban));

        // when 
        transferService.transfer(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, receivingName, reference, now);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, sendingName, reference, now);
        
        assertEquals(-transferAmount, sendingAccount.balance());
        assertEquals(transferAmount, receivingAccount.balance());
    }

    @Test
    public void given_customers_and_girosending_savingreceiving_when_transfer_then_throws() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new SavingsAccount(receivingCustomer.customerId(), receivingIban));

        // when ... then
        assertThrows(AccountException.class, () -> transferService.transfer(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount));
    }

    @Test
    public void given_customers_and_savingssending_giroreceiving_when_transfer_then_throws() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new SavingsAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(receivingCustomer.customerId(), receivingIban));

        // when ... then
        assertThrows(AccountException.class, () -> transferService.transfer(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount));
    }

    @Test
    public void given_samecustomer_and_girosending_savingreceiving_when_transfer_then_exchangebalance() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String name = "Jonathan";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer customer = new Customer(new CustomerId("1"), name);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(customer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new SavingsAccount(customer.customerId(), receivingIban));

        // when 
        transferService.transfer(transferAmount, reference, now, customer, sendingAccount, customer, receivingAccount);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, name, reference, now);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, name, reference, now);

        assertEquals(-transferAmount, sendingAccount.balance());
        assertEquals(transferAmount, receivingAccount.balance());
    }

    @Test
    public void given_samecustomer_and_savingssending_giroreceiving_when_transfer_then_exchangebalance() throws AccountException {
        // given
        double initialTransferAmount = 1000;
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String name = "Jonathan";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer customer = new Customer(new CustomerId("1"), name);

        Account sendingAccount = Mockito.spy(
            new SavingsAccount(customer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(customer.customerId(), receivingIban));

        // when
        transferService.transfer(initialTransferAmount, reference, now, customer, receivingAccount, customer, sendingAccount);
        transferService.transfer(transferAmount, reference, now, customer, sendingAccount, customer, receivingAccount);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, name, reference, now);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, name, reference, now);
        
        assertEquals(transferAmount, sendingAccount.balance());
        assertEquals(-transferAmount, receivingAccount.balance());
    }

    @Test
    public void given_customers_when_transfermorethan5000_then_throws() throws AccountException {
        // given
        double transferAmount = 5001;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(receivingCustomer.customerId(), receivingIban));

        sendingAccount.deposit(10_000, now);

        // when ... then
        assertThrows(AccountException.class, () -> transferService.transfer(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount));
    }

    @Test
    public void given_samecustomer_when_transfermorethan5000_then_exchangebalance() throws AccountException {
        // given
        double initialSendingBalance = 10_000;
        double transferAmount = 5001;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String name = "Jonathan";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer customer = new Customer(new CustomerId("1"), name);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(customer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(customer.customerId(), receivingIban));

        sendingAccount.deposit(initialSendingBalance, now);

        // when 
        transferService.transfer(transferAmount, reference, now, customer, sendingAccount, customer, receivingAccount);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, name, reference, now);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, name, reference, now);

        assertEquals(initialSendingBalance - transferAmount, sendingAccount.balance());
        assertEquals(transferAmount, receivingAccount.balance());
    }

    @Test
    public void given_customers_and_accounts_when_transferSend_then_reduceSendingBalance() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(receivingCustomer.customerId(), receivingIban));

        // when 
        transferService.transferSend(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, receivingName, reference, now); 
        assertEquals(-transferAmount, sendingAccount.balance());
    }

    @Test
    public void given_customers_and_accounts_when_transferReceive_then_increaseReceivingBalance() throws AccountException {
        // given
        double transferAmount = 500;
        String reference = "Rent";
        LocalDateTime now = LocalDateTime.now();

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(
            new GiroAccount(sendingCustomer.customerId(), sendingIban));
        Account receivingAccount = Mockito.spy(
            new GiroAccount(receivingCustomer.customerId(), receivingIban));

        // when 
        transferService.transferReceive(transferAmount, reference, now, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        // then
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, sendingName, reference, now);
        assertEquals(transferAmount, receivingAccount.balance());
    }
}
