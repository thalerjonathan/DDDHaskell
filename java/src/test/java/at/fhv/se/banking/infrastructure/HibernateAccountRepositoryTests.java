package at.fhv.se.banking.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.repositories.AccountRepository;

public class HibernateAccountRepositoryTests {
    
    private AccountRepository repo;

    @BeforeEach
    public void beforeEach() {
        this.repo = new HibernateAccountRepository();
    }

    @Test
    public void given_account_when_add_then_fetch() {
        // given
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = new Account(new CustomerId("1"), iban, AccountType.GIRO);
        account.deposit(1234);
        account.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
    
        // when
        repo.add(account);

        // then
        Optional<Account> actualAccount = repo.byIban(iban);
        compareAccounts(account, actualAccount.get());
    }

    @Test
    public void given_accounts_when_add_and_forcustomer_then_fetch() {
        // given
        CustomerId cId1 = new CustomerId("1");
        CustomerId cId2 = new CustomerId("2");

        Account account1 = new Account(cId1, new Iban("AT12 3456 7890 1234"), AccountType.GIRO);
        account1.deposit(1234);
        account1.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
        Account account2 = new Account(cId1, new Iban("AT23 4567 8901 2345"), AccountType.GIRO);
        account2.deposit(3456);
        account2.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
        Account account3 = new Account(cId2, new Iban("AT34 5678 9012 3456"), AccountType.GIRO);
        account3.deposit(3456);
        account3.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
    
        // when
        repo.add(account1);
        repo.add(account2);
        repo.add(account3);
        List<Account> customer1Accounts = repo.forCustomer(cId1);
        List<Account> customer2Accounts = repo.forCustomer(cId2);

        // then
        assertEquals(2, customer1Accounts.size());
        compareAccounts(account1, customer1Accounts.get(0));
        compareAccounts(account2, customer1Accounts.get(1));

        assertEquals(1, customer2Accounts.size());
        compareAccounts(account3, customer2Accounts.get(0));
    }

    private static void compareAccounts(Account expected, Account actual) {
        assertEquals(expected.owner(), actual.owner());
        assertEquals(expected.iban(), actual.iban());
        assertEquals(expected.balance(), actual.balance());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.transactions(), actual.transactions());
    }
}
