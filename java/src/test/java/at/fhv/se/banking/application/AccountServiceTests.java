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
import at.fhv.se.banking.application.api.TimeService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
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

    @MockBean
    private TimeService timeService;

    @Test
    public void given_accountinrepo_when_byIban_thenreturn() throws AccountNotFoundException, AccountException {
        // given
        LocalDateTime now = LocalDateTime.now();
        double deposit = 1234;
        double receive = 100;
        double withdraw = 500;
        double transfer = 100;
        double balance = deposit + receive - withdraw - transfer;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Iban receiveIban = new Iban("AT98 7654 3210 9876");
        Iban transferIban = new Iban("AT98 7654 3210 9876");

        Account account = new GiroAccount(new CustomerId("1"), iban);
        account.deposit(deposit, now);
        account.receiveFrom(receiveIban, 100.0, "Max Mustermann", "Rent", now);
        account.withdraw(withdraw, now);
        account.transferTo(transferIban, 100.0, "Max Mustermann", "Rent", now);

        AccountDTO expectedAccountDTO = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withBalance(balance)
                .withIban(iban)
                .withType(account.type())
                .build())
            .addTXLine(TXLineDTO.builder()
                .ofAmount(deposit)
                .atTime(now)
                .withIban(iban)
                .withName("Deposit")
                .withReference("Deposit")
                .build())
            .addTXLine(TXLineDTO.builder()
                .ofAmount(receive)
                .atTime(now)
                .withIban(receiveIban)
                .withName("Max Mustermann")
                .withReference("Rent")
                .build())
            .addTXLine(TXLineDTO.builder()
                .ofAmount(-withdraw)
                .atTime(now)
                .withIban(iban)
                .withName("Withdraw")
                .withReference("Withdraw")
                .build())
            .addTXLine(TXLineDTO.builder()
                .ofAmount(-transfer)
                .atTime(now)
                .withIban(transferIban)
                .withName("Max Mustermann")
                .withReference("Rent")
                .build())
            .build();

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));
        Mockito.when(timeService.utcNow()).thenReturn(now);

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
    public void given_accountinrepo_when_deposit_thenincreasebalance() throws AccountNotFoundException, InvalidOperationException, AccountException {
        // given
        LocalDateTime now = LocalDateTime.now();
        double depositAmount = 1000;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = Mockito.spy(new GiroAccount(new CustomerId("1"), iban));

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));
        Mockito.when(timeService.utcNow()).thenReturn(now);

        // when
        this.accountSerivce.deposit(iban.toString(), depositAmount);

        // then
        Mockito.verify(account).deposit(depositAmount, now);
        assertEquals(depositAmount, account.balance());
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
    public void given_accountinrepo_when_withdraw_thendecreasebalance() throws AccountNotFoundException, AccountException, InvalidOperationException {
        // given
        double withdrawAmount = 123;
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = Mockito.spy(new GiroAccount(new CustomerId("1"), iban));
        LocalDateTime now = LocalDateTime.now();

        Mockito.when(accountRepo.byIban(iban)).thenReturn(Optional.of(account));
        Mockito.when(timeService.utcNow()).thenReturn(now);

        // when
        this.accountSerivce.withdraw(iban.toString(), withdrawAmount);

        // then
        Mockito.verify(account).withdraw(withdrawAmount, now);
        assertEquals(-withdrawAmount, account.balance());
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
    public void given_accountinrepo_when_transfer_thenexchangedbalances() throws AccountNotFoundException, CustomerNotFoundException, AccountException, InvalidOperationException {
        // given
        double transferAmount = 500;
        String reference = "Rent";

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

        LocalDateTime now = LocalDateTime.now();

        Mockito.when(timeService.utcNow()).thenReturn(now);

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byId(sendingCustomer.customerId())).thenReturn(Optional.of(sendingCustomer));
        Mockito.when(customerRepo.byId(receivingCustomer.customerId())).thenReturn(Optional.of(receivingCustomer));

        // when
        this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference);

        // then
        Mockito.verify(sendingAccount).transferTo(receivingIban, transferAmount, receivingName, reference, now);
        Mockito.verify(receivingAccount).receiveFrom(sendingIban, transferAmount, sendingName, reference, now);
        assertEquals(-transferAmount, sendingAccount.balance());
        assertEquals(transferAmount, receivingAccount.balance());
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
    public void given_receivingaccountnotinrepo_when_transfer_thenthrows() throws AccountNotFoundException, AccountException {
        // given
        LocalDateTime now = LocalDateTime.now();
        double transferAmount = 1000;
        String reference = "Rent";
        Iban sendingIban = new Iban("AT12 3456 7890 1234");
        Iban receivingIban = new Iban("AT98 7654 3210 9876");

        Account sendingAccount = new GiroAccount(new CustomerId("1"), sendingIban);
        sendingAccount.deposit(1234, now);

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

        CustomerId sendingCustomerId = new CustomerId("1");
        CustomerId receivingCustomerId = new CustomerId("2");

        Account sendingAccount = new GiroAccount(sendingCustomerId, sendingIban);
        Account receivingAccount = new GiroAccount(receivingCustomerId, receivingIban);

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byId(sendingCustomerId)).thenReturn(Optional.empty());

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

        CustomerId sendingCustomerId = new CustomerId("1");
        CustomerId receivingCustomerId = new CustomerId("2");

        Account sendingAccount = new GiroAccount(sendingCustomerId, sendingIban);
        Account receivingAccount = new GiroAccount(receivingCustomerId, receivingIban);

        Customer sendingCustomer = new Customer(new CustomerId("1"), "Jonathan");

        Mockito.when(accountRepo.byIban(sendingIban)).thenReturn(Optional.of(sendingAccount));
        Mockito.when(accountRepo.byIban(receivingIban)).thenReturn(Optional.of(receivingAccount));

        Mockito.when(customerRepo.byId(sendingCustomerId)).thenReturn(Optional.of(sendingCustomer));
        Mockito.when(customerRepo.byId(receivingCustomerId)).thenReturn(Optional.empty());

        // when ... then
        assertThrows(CustomerNotFoundException.class, () -> this.accountSerivce.transfer(sendingIban.toString(), receivingIban.toString(), transferAmount, reference));
    }

    // TODO: test violations of business rules which throw exceptions
}
