package at.fhv.se.banking.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;
import at.fhv.se.banking.domain.services.api.TransferService;

@SpringBootTest
public class CustomerServiceTests {
    
    @Autowired
    private CustomerService customerService;

    @SuppressWarnings("unused")
    @Autowired
    private TransferService transferService;

    @MockBean
    private CustomerRepository customerRepo;

    @MockBean
    private AccountRepository accountRepo;

    @Test
    public void given_customersinrepo_when_listall_thenreturnall() {
        // given
        List<Customer> customers = Arrays.asList(
                new Customer(new CustomerId("1"), "Jonathan"),
                new Customer(new CustomerId("2"), "Thomas")
            );

        List<CustomerDetailsDTO> expectedCustomerDTOs = customers.stream().map(c -> 
            CustomerDetailsDTO.builder()
                .withId(c.customerId())
                .withName(c.name())
                .build()).collect(Collectors.toList());

        Mockito.when(customerRepo.all()).thenReturn(customers);
        
        // then
        List<CustomerDetailsDTO> actualCustomerDTOs = customerService.listAll();

        // then
        assertEquals(expectedCustomerDTOs, actualCustomerDTOs);
    }

    @Test
    public void given_customerinrepo_when_informationFor_thenreturninfo() throws CustomerNotFoundException, AccountException {
        // given
        String customerName = "Jonathan";
        CustomerId customerId = new CustomerId("42");
        Customer customer = new Customer(customerId, customerName);
        double balance = 1234;

        CustomerDTO expectedInfoDTO = CustomerDTO.builder()
            .withCustomer(CustomerDetailsDTO.builder()
                .withId(customerId)
                .withName(customerName)
                .build())
            .addAccount(AccountDetailsDTO.builder()
                .withBalance(balance)
                .withIban(new Iban("AT12 3456 7890 1234"))
                .withType("GIRO")
                .build()
            ).build();

        Account a = new GiroAccount(customerId, new Iban("AT12 3456 7890 1234"));
        a.deposit(balance, LocalDateTime.now());
        
        List<Account> accounts = Arrays.asList(a);

        Mockito.when(customerRepo.byId(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(accountRepo.forCustomer(customerId)).thenReturn(accounts);

        // when
        CustomerDTO actualInfoDTO = customerService.detailsFor(customerId.id());

        // then
        assertEquals(expectedInfoDTO, actualInfoDTO);
    }

    @Test
    public void given_nocustomerinrepo_when_informationFor_thenthrowsexception() {
        // given
        CustomerId customerId = new CustomerId("42");
        Mockito.when(customerRepo.byId(customerId)).thenReturn(Optional.empty());
       
        // when ... then
        assertThrows(CustomerNotFoundException.class, () -> customerService.detailsFor(customerId.id()));
    }
}
