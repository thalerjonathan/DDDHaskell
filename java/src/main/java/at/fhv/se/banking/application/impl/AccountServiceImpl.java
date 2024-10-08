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
import at.fhv.se.banking.domain.events.TransferFailed;
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
    public void deposit(double amount, String ibanStr) throws AccountNotFoundException, InvalidOperationException {
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
    public void withdraw(double amount, String ibanStr) throws AccountNotFoundException, InvalidOperationException {
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
    public void transferTransactional(double amount, String reference, String sendingIbanStr, String receivingIbanStr) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException {
        this.transfer(amount, reference, sendingIbanStr, receivingIbanStr, true);
    }

    @Transactional
    @Override
    public void transferEventual(double amount, String reference, String sendingIbanStr, String receivingIbanStr) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException {
        this.transfer(amount, reference, sendingIbanStr, receivingIbanStr, false);
    }

    @Transactional
    @Override
    public void transferReceive(TransferSent transferSent) {
        Iban sendingIban = transferSent.sendingAccount();
        Iban receivingIban = transferSent.receivingAccount();

        Optional<Account> sendingAccountOpt = accountRepo.byIban(sendingIban);
        if (sendingAccountOpt.isEmpty()) {
            transferFailedEvent("Couldn't find account of sending IBAN " + sendingIban, transferSent);
            return;
        }

        Optional<Account> receivingAccountOpt = accountRepo.byIban(receivingIban);
        if (receivingAccountOpt.isEmpty()) {
            transferFailedEvent("Couldn't find account of receiving IBAN " + receivingIban, transferSent);
            return;
        }

        Account sendingAccount = sendingAccountOpt.get();
        Account receivingAccount = receivingAccountOpt.get();

        Optional<Customer> sendingCustomerOpt = customerRepo.byId(sendingAccount.owner());
        if (sendingCustomerOpt.isEmpty()) {
            transferFailedEvent("Couldn't find a customer for sending IBAN " + sendingIban, transferSent);
            return;
        }

        Optional<Customer> receivingCustomerOpt = customerRepo.byId(receivingAccount.owner());
        if (receivingCustomerOpt.isEmpty()) {
            transferFailedEvent("Couldn't find a customer for receiving IBAN " + receivingIban, transferSent);
            return;
        }

        Customer sendingCustomer = sendingCustomerOpt.get();
        Customer receivingCustomer = receivingCustomerOpt.get();

        try {
            this.transferService.transferReceive(
                transferSent.amount(), 
                transferSent.reference(), 
                timeService.utcNow(), 
                sendingCustomer, 
                sendingAccount, 
                receivingCustomer, 
                receivingAccount);
        } catch (Exception e) {
            transferFailedEvent("Invalid Operation in receiving transfer: " + e.getMessage(), transferSent);
        }    
    }

    @Transactional
    @Override
    public void transferFailed(TransferFailed transferFailed) {
        Iban sendingIban = transferFailed.sendingAccount();

        Optional<Account> sendingAccountOpt = accountRepo.byIban(sendingIban);
        if (sendingAccountOpt.isEmpty()) {
            // NOTE: don't send DomainEvent again, something is deeply broken
            // NOTE: in real application need proper handling
            return;
        }

        Account sendingAccount = sendingAccountOpt.get();

        Optional<Customer> sendingCustomerOpt = customerRepo.byId(sendingAccount.owner());
        if (sendingCustomerOpt.isEmpty()) {
            // NOTE: don't send DomainEvent again, something is deeply broken
            // NOTE: in real application need proper handling
            return;
        }

        Customer sendingCustomer = sendingCustomerOpt.get();
        
        // NOTE: would add some notification to Customer

        // transfer back to sending account because was removed before
        sendingAccount.receiveFrom(
            sendingIban,
            transferFailed.amount(), 
            sendingCustomer.name(),
            transferFailed.reference(), 
            timeService.utcNow());
    }

    private void transferFailedEvent(String error, TransferSent sent) {
        DomainEvent transferFailed = new TransferFailed(error, sent);

        try {
            this.eventRepo.persistDomainEvent(transferFailed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transfer(double amount, String reference, String sendingIbanStr, String receivingIbanStr, boolean transactional) throws AccountNotFoundException, CustomerNotFoundException, InvalidOperationException {
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
            if (transactional) {
                this.transferService.transfer(
                    amount, 
                    reference, 
                    timeService.utcNow(), 
                    sendingCustomer, 
                    sendingAccount, 
                    receivingCustomer, 
                    receivingAccount);

            } else {
                this.transferService.transferSend(
                    amount, 
                    reference, 
                    timeService.utcNow(), 
                    sendingCustomer, 
                    sendingAccount, 
                    receivingCustomer, 
                    receivingAccount);

                DomainEvent transferSent = new TransferSent(
                    amount,
                    reference,
                    sendingCustomer.customerId(), 
                    receivingCustomer.customerId(),
                    sendingAccount.iban(),
                    receivingAccount.iban());
                    
                eventRepo.persistDomainEvent(transferSent);
            }
        } catch (Exception e) {
            throw new InvalidOperationException(e.getMessage());
        }
    }
}
