package at.fhv.se.banking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.banking.application.api.BankingService;
import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@SpringBootTest
public class BankingServiceTests {
    
    @Autowired
    private BankingService bankingService;

    @MockBean
    private CustomerRepository customerRepo;

    @MockBean
    private AccountRepository accountRepo;

    @Test
    public void given_customernameandaccountsinrepo_when_listAccountsForCustomer_thenreturnAccounts() {
        // given
        String customerName = "jonathan";
        CustomerId customerId = new CustomerId(1);
        Customer customer = new Customer();
        List<Account> expectedAccounts = Arrays.asList(new Account(), new Account());

        Mockito.when(customerRepo.byName(customerName)).thenReturn(Optional.of(customer));
        Mockito.when(accountRepo.forCustomer(customerId)).thenReturn(expectedAccounts);

        // when
        List<Account> actualAccounts = bankingService.listAccountsForCustomer(customerName);

        // then
        assertEquals(expectedAccounts,  actualAccounts);
    }
}
