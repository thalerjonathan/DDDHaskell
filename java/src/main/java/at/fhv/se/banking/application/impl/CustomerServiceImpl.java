package at.fhv.se.banking.application.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public List<CustomerDetailsDTO> listAll() {
        List<Customer> customers = this.customerRepo.all();
        return customers.stream().map(c -> CustomerDetailsDTO.builder()
            .withId(c.customerId())
            .withName(c.name())
            .build()).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO detailsFor(String id) throws CustomerNotFoundException {
        CustomerId customerId = new CustomerId(id);
        Optional<Customer> customerOpt = this.customerRepo.byId(customerId);
        if (customerOpt.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        Customer customer = customerOpt.get();
        List<Account> accounts = accountRepo.forCustomer(customerId);

        CustomerDTO.Builder builder = CustomerDTO.builder();
        builder.withCustomer(CustomerDetailsDTO.builder()
            .withId(customerId)
            .withName(customer.name())
            .build());

        for (Account a : accounts) {
            builder.addAccount(AccountDetailsDTO.builder()
                .withBalance(a.balance())
                .withIban(a.iban())
                .withType(a.type())
                .build());
        }

        return builder.build();
    }
    
}
