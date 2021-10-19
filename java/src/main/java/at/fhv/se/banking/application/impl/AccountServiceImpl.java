package at.fhv.se.banking.application.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.model.TXLine;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class AccountServiceImpl implements AccountService {

    @Autowired
    private Clock clock;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public AccountDTO accountByIban(String ibanStr) throws AccountNotFoundException {
        Iban iban = new Iban(ibanStr);
        Optional<Account> accountOpt = accountRepo.byIban(iban);
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account for IBAN " + ibanStr);
        }

        Account account = accountOpt.get();

        AccountDTO.Builder builder = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withBalance(account.balance())
                .withIban(account.iban())
                .withType(account.type())
                .build());

        for (TXLine tx : account.transactions()) {
            builder.addTXLine(TXLineDTO.builder()
                .ofAmount(tx.amount())
                .atTime(tx.time())
                .withIban(tx.iban())
                .withName(tx.name())
                .withReference(tx.reference())
                .build());
        }

        return builder.build();
    }

    @Override
    public void deposit(String ibanStr, double amount) throws AccountNotFoundException {
        Iban iban = new Iban(ibanStr);
        Optional<Account> accountOpt = accountRepo.byIban(iban);
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of deposit IBAN " + ibanStr);
        }

        Account account = accountOpt.get();
        account.deposit(amount);
    }

    @Override
    public void withdraw(String ibanStr, double amount) throws AccountNotFoundException {
        Iban iban = new Iban(ibanStr);
        Optional<Account> accountOpt = accountRepo.byIban(iban);
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of withdraw IBAN " + ibanStr);
        }

        Account account = accountOpt.get();
        account.withdraw(amount);
    }

    @Override
    public void transfer(String sendingIbanStr, String receivingIbanStr, double amount, String reference) throws AccountNotFoundException, CustomerNotFoundException {
        Iban sendingIban = new Iban(sendingIbanStr);
        Iban receivingIban = new Iban(receivingIbanStr);

        Optional<Account> sendingAccountOpt = accountRepo.byIban(sendingIban);
        if (sendingAccountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of sending IBAN " + sendingIbanStr);
        }

        Optional<Account> receivingAccountOpt = accountRepo.byIban(receivingIban);
        if (receivingAccountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of receiving IBAN " + receivingIbanStr);
        }

        Account sendingAccount = sendingAccountOpt.get();
        Account receivingAccount = receivingAccountOpt.get();

        Optional<Customer> sendingCustomerOpt = customerRepo.byId(sendingAccount.owner());
        if (sendingCustomerOpt.isEmpty()) {
            throw new CustomerNotFoundException("Couldn't find a customer for sending IBAN " + sendingIbanStr);
        }

        Optional<Customer> receivingCustomerOpt = customerRepo.byId(receivingAccount.owner());
        if (receivingCustomerOpt.isEmpty()) {
            throw new CustomerNotFoundException("Couldn't find a customer for receiving IBAN " + receivingIbanStr);
        }

        Customer sendingCustomer = sendingCustomerOpt.get();
        Customer receivingCustomer = receivingCustomerOpt.get();

        sendingAccount.transferTo(receivingIban, amount, receivingCustomer.name(), reference, LocalDateTime.now(clock));
        receivingAccount.receiveFrom(sendingIban, amount, sendingCustomer.name(), reference, LocalDateTime.now(clock));
    }
}
