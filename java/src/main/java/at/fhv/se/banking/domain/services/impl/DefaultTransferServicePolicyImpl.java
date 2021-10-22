package at.fhv.se.banking.domain.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.services.api.TransferService;

@Component
public class DefaultTransferServicePolicyImpl implements TransferService {

    @Override
    public void transfer(double amount, String reference, LocalDateTime time, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException {

        sendingAccount.transferTo(receivingAccount.iban(), amount, receivingCustomer.name(), reference, time);
        receivingAccount.receiveFrom(sendingAccount.iban(), amount, sendingCustomer.name(), reference, time);
    }
}
