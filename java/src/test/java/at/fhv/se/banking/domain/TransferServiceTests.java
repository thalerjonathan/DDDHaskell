package at.fhv.se.banking.domain;

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
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.services.api.TransferService;
import at.fhv.se.banking.domain.services.impl.DefaultTransferServicePolicyImpl;

public class TransferServiceTests {
    
    // NOTE: not using dependency injection, want to keep tests fast
    private TransferService transferService;

    @BeforeEach
    public void beforeEach() {
        this.transferService = new DefaultTransferServicePolicyImpl();
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
}
