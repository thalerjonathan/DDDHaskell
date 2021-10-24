package at.fhv.se.banking.infrastructure.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class HibernateAccountRepositoryTests {
    
    @Autowired
    private HibernateAccountRepository repo;

    @Test
    public void given_account_when_add_then_fetch() throws AccountException {
        // given
        Iban iban = new Iban("AT12 3456 7890 1234");
        Account account = new GiroAccount(new CustomerId("1"), iban);
        account.deposit(1234, LocalDateTime.now());
        account.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
    
        // when
        repo.add(account);

        // then
        Optional<Account> actualAccount = repo.byIban(iban);
        compareAccounts(account, actualAccount.get());
    }

    @Test
    public void given_accounts_when_add_and_forcustomer_then_fetch() throws AccountException {
        // given
        CustomerId cId1 = new CustomerId("1");
        CustomerId cId2 = new CustomerId("2");

        Account account1 = new GiroAccount(cId1, new Iban("AT12 3456 7890 1234"));
        account1.deposit(1234, LocalDateTime.now());
        account1.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
        Account account2 = new GiroAccount(cId1, new Iban("AT23 4567 8901 2345"));
        account2.deposit(3456, LocalDateTime.now());
        account2.receiveFrom(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now());
        Account account3 = new GiroAccount(cId2, new Iban("AT34 5678 9012 3456"));
        account3.deposit(3456, LocalDateTime.now());
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
