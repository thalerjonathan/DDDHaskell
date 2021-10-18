package at.fhv.se.banking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@SpringBootTest
public class CustomerServiceTests {
    
    @Autowired
    private CustomerService customerService;

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

        List<CustomerDTO> expectedCustomerDTOs = customers.stream().map(c -> 
            CustomerDTO.builder()
                .withId(c.customerId())
                .withName(c.name())
                .build()).collect(Collectors.toList());

        Mockito.when(customerRepo.all()).thenReturn(customers);
        
        // then
        List<CustomerDTO> actualCustomerDTOs = customerService.listAll();

        // then
        assertEquals(expectedCustomerDTOs, actualCustomerDTOs);
    }

    @Test
    public void given_customerinrepo_when_informationFor_thenreturninfo() throws CustomerNotFoundException {
        // given
        String customerName = "Jonathan";
        CustomerId customerId = new CustomerId("42");
        Customer customer = new Customer(customerId, customerName);

        CustomerDetailsDTO expectedInfoDTO = CustomerDetailsDTO.builder()
            .withCustomer(CustomerDTO.builder()
                .withId(customerId)
                .withName(customerName)
                .build())
            .addAccount(AccountDetailsDTO.builder()
                .withBalance(1234)
                .withIban(new Iban("AT12 3456 7890 1234"))
                .withType(AccountType.GIRO)
                .build()
            ).build();

        List<Account> accounts = Arrays.asList(
            new Account(customerId, new Iban("AT12 3456 7890 1234"), AccountType.GIRO, 1234)
        );

        Mockito.when(customerRepo.byId(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(accountRepo.forCustomer(customerId)).thenReturn(accounts);

        // when
        CustomerDetailsDTO actualInfoDTO = customerService.detailsFor(customerId.id());

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
