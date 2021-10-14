package at.fhv.se.banking.application.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountInfoDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerInfoDTO;
import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public List<CustomerDTO> listAll() {
        List<Customer> customers = this.customerRepo.all();
        return customers.stream().map(c -> CustomerDTO.create().withName(c.name()).build()).collect(Collectors.toList());
    }

    @Override
    public CustomerInfoDTO informationFor(String id) throws CustomerNotFoundException {
        CustomerId customerId = new CustomerId(id);
        Optional<Customer> customerOpt = this.customerRepo.byId(customerId);
        if (customerOpt.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        Customer customer = customerOpt.get();
        List<Account> accounts = accountRepo.forCustomer(customerId);

        CustomerInfoDTO.Builder builder = CustomerInfoDTO.create();
        builder.withCustomer(CustomerDTO.create()
            .withName(customer.name())
            .build());

        for (Account a : accounts) {
            builder.addAccountInfo(AccountInfoDTO.create()
                .withBalance(a.balance())
                .withIban(a.iban())
                .withType(a.type())
                .build());
        }

        return builder.build();
    }
    
}
