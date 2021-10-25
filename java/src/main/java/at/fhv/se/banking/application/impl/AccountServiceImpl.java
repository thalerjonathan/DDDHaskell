package at.fhv.se.banking.application.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.TimeService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.events.DomainEvent;
import at.fhv.se.banking.domain.events.TransferSent;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.TXLine;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;
import at.fhv.se.banking.domain.repositories.EventRepository;
import at.fhv.se.banking.domain.services.api.TransferService;

@Component
public class AccountServiceImpl implements AccountService {

    @Autowired
    private TimeService timeService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private TransferService transferService;

    @Autowired
    private EventRepository eventRepo;

    @Transactional(readOnly = true)
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

    @Transactional
    @Override
    public void deposit(String ibanStr, double amount) throws AccountNotFoundException, InvalidOperationException {
        Iban iban = new Iban(ibanStr);
        Optional<Account> accountOpt = accountRepo.byIban(iban);
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of deposit IBAN " + ibanStr);
        }

        Account account = accountOpt.get();

        try {
            account.deposit(amount, timeService.utcNow());
        } catch (AccountException e) {
            throw new InvalidOperationException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void withdraw(String ibanStr, double amount) throws AccountNotFoundException, InvalidOperationException {
        Iban iban = new Iban(ibanStr);
        Optional<Account> accountOpt = accountRepo.byIban(iban);
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Couldn't find account of withdraw IBAN " + ibanStr);
        }

        Account account = accountOpt.get();

        try {
            account.withdraw(amount, timeService.utcNow());
        } catch (AccountException e) {
            throw new InvalidOperationException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void transfer(String sendingIbanStr, String receivingIbanStr, double amount, String reference) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException {
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

        try {
            this.transferService.transfer(amount, reference, timeService.utcNow(), sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);
            
            DomainEvent transferSent = new TransferSent(
                amount,
                reference,
                sendingCustomer.customerId(), 
                receivingCustomer.customerId(),
                sendingAccount.iban(),
                receivingAccount.iban());
                
            eventRepo.persistDomainEvent(transferSent);
        } catch (Exception e) {
            throw new InvalidOperationException(e.getMessage());
        }
    }
}
