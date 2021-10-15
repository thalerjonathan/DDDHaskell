package at.fhv.se.banking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.model.TXLine;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@SpringBootTest
public class AccountServiceTests {
    
    @Autowired
    private AccountService accountSerivce;

    @MockBean 
    private AccountRepository accountRepo;

    @MockBean 
    private CustomerRepository customerRepo;

    @Test
    public void given_accountinrepo_when_byIban_thenreturn() throws AccountNotFoundException {
        // given
        LocalDateTime txLineTime = LocalDateTime.now();
        double balance = 1234.0;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = new Account(iban, AccountType.GIRO, balance);
        account.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 100.0, "Max Mustermann", "Rent", txLineTime));

        AccountDTO expectedAccountDTO = AccountDTO.create()
            .withDeails(AccountDetailsDTO.create()
                .withBalance(balance)
                .withIban(iban)
                .withType(AccountType.GIRO)
                .build())
            .addTXLine(TXLineDTO.create()
                .ofAmount(100)
                .atTime(txLineTime)
                .withIban(new Iban("AT98 7654 3210 9876"))
                .withName("Max Mustermann")
                .withReference("Rent")
                .build())
            .build();

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));

        // when
        AccountDTO actualAccountDTO = this.accountSerivce.accountByIban(iban.toString());

        // then
        assertEquals(expectedAccountDTO, actualAccountDTO);
    }

    @Test
    public void given_nomatchingaccountinrepo_when_byIban_thenthrows() {
        // given
        Iban iban = new Iban("AT12 3456 7890 1234");
        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(AccountNotFoundException.class, () -> this.accountSerivce.accountByIban(iban.toString()));
    }

    @Test
    public void given_accountinrepo_when_deposit_thenincreasebalance() throws AccountNotFoundException {
        // given
        double depositAmount = 1000;
        double balance = 1234.0;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = Mockito.spy(new Account(iban, AccountType.GIRO, balance));

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));

        // when
        this.accountSerivce.deposit(iban.toString(), depositAmount);

        // then
        Mockito.verify(account).deposit(depositAmount);
        assertEquals(balance + depositAmount, account.balance());
    }

    @Test
    public void given_noaccountinrepo_when_deposit_thenthrows() throws AccountNotFoundException {
        // given
        double depositAmount = 1000;
        Iban iban = new Iban("AT12 3456 7890 1234");

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(AccountNotFoundException.class, () -> this.accountSerivce.deposit(iban.toString(), depositAmount));
    }

    @Test
    public void given_accountinrepo_when_withdraw_thendecreasebalance() throws AccountNotFoundException {
        // given
        double withdrawAmount = 1000;
        double balance = 1234.0;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = Mockito.spy(new Account(iban, AccountType.GIRO, balance));

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));

        // when
        this.accountSerivce.withdraw(iban.toString(), withdrawAmount);

        // then
        Mockito.verify(account).withdraw(withdrawAmount);
        assertEquals(balance - withdrawAmount, account.balance());
    }

    @Test
    public void given_noaccountinrepo_when_withdraw_thenthrows() throws AccountNotFoundException {
        // given
        double withdrawAmount = 1000;
        Iban iban = new Iban("AT12 3456 7890 1234");

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(AccountNotFoundException.class, () -> this.accountSerivce.withdraw(iban.toString(), withdrawAmount));
    }

    @Test
    public void given_accountinrepo_when_transfer_thenexchangedbalances() throws AccountNotFoundException, CustomerNotFoundException {
        // given
        double transferAmount = 1000;
        String reference = "Rent";

        String sendingName = "Jonathan";
        String receivingName = "Thomas";
        double sendingAccountBalance = 1234.0;
        double receivingAccountBalance = 2345.0;
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Customer sendingCustomer = new Customer(new CustomerId("1"), sendingName);
        Customer receivingCustomer = new Customer(new CustomerId("2"), receivingName);

        Account sendingAccount = Mockito.spy(new Account(sendingIban, AccountType.GIRO, sendingAccountBalance));
        Account receivingAccount = Mockito.spy(new Account(receivingIban, AccountType.GIRO, receivingAccountBalance));

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingCustomer));
        Mockito.when(customerRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingCustomer));

        // when
        this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, receivingName, reference);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, sendingName, reference);
        assertEquals(sendingAccountBalance - transferAmount, sendingAccount.balance());
        assertEquals(receivingAccountBalance + transferAmount, receivingAccount.balance());
    }

    @Test
    public void given_sendingaccountnotinrepo_when_transfer_thenthrows() throws AccountNotFoundException {
        // given
        double transferAmount = 1000;
        String reference = "Rent";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(AccountNotFoundException.class, () -> this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference));
    }

    @Test
    public void given_receivingaccountnotinrepo_when_transfer_thenthrows() throws AccountNotFoundException {
        // given
        double transferAmount = 1000;
        String reference = "Rent";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Account sendingAccount = new Account(sendingIban, AccountType.GIRO, 1234);

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(AccountNotFoundException.class, () -> this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference));
    }

    @Test
    public void given_sendingcustomernotinrepo_when_transfer_thenthrows() throws AccountNotFoundException {
        // given
        double transferAmount = 1000;
        String reference = "Rent";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Account sendingAccount = new Account(sendingIban, AccountType.GIRO, 1234);
        Account receivingAccount = new Account(receivingIban, AccountType.GIRO, 2345);

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byIban(sendingIban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(CustomerNotFoundException.class, () -> this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference));
    }

    @Test
    public void given_receivingcustomernotinrepo_when_transfer_thenthrows() throws AccountNotFoundException {
        // given
        double transferAmount = 1000;
        String reference = "Rent";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Account sendingAccount = new Account(sendingIban, AccountType.GIRO, 1234);
        Account receivingAccount = new Account(receivingIban, AccountType.GIRO, 2345);

        Customer sendingCustomer = new Customer(new CustomerId("1"), "Jonathan");

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingCustomer));
        Mockito.when(customerRepo.byIban(receivingIban)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(CustomerNotFoundException.class, () -> this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference));
    }
}
