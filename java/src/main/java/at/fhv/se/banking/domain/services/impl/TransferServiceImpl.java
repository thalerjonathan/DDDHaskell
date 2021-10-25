package at.fhv.se.banking.domain.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.services.api.TransferService;

@Component
public class TransferServiceImpl implements TransferService {

    private final static double TRANSFER_LIMIT = 5000;

    @Override
    public void transfer(double amount, String reference, LocalDateTime time, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException {

        checkTransferRules(amount, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        sendingAccount.transferTo(receivingAccount.iban(), amount, receivingCustomer.name(), reference, time);
        receivingAccount.receiveFrom(sendingAccount.iban(), amount, sendingCustomer.name(), reference, time);
    }

    @Override
    public void transferSend(double amount, String reference, LocalDateTime time, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException {
        
        checkTransferRules(amount, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        sendingAccount.transferTo(receivingAccount.iban(), amount, receivingCustomer.name(), reference, time);
    }

    @Override
    public void transferReceive(double amount, String reference, LocalDateTime time, Customer sendingCustomer,
            Account sendingAccount, Customer receivingCustomer, Account receivingAccount) throws AccountException {

        checkTransferRules(amount, sendingCustomer, sendingAccount, receivingCustomer, receivingAccount);

        receivingAccount.receiveFrom(sendingAccount.iban(), amount, sendingCustomer.name(), reference, time);
    }

    private static void checkTransferRules(
            double amount, 
            Customer sendingCustomer,
            Account sendingAccount, 
            Customer receivingCustomer, 
            Account receivingAccount) throws AccountException {
        if (notTransferSameCustomer(sendingCustomer, receivingCustomer)) {
            if (isSavings(sendingAccount)) {
                throw new AccountException("Cannot transfer from Savings account!");
            } else if (isSavings(receivingAccount)) {
                throw new AccountException("Cannot transfer to Savings account!");
            }
        
            if (amount > TRANSFER_LIMIT) {
                throw new AccountException("Cannot transfer more than 5000€!"); 
            }
        }
    }
    
    private static boolean notTransferSameCustomer(Customer sendingCustomer, Customer receivingCustomer) {
        return false == sendingCustomer.equals(receivingCustomer);
    }

    private static boolean isSavings(Account a) {
        // TODO: this is bad style, refactor (into visitor pattern/double dynamic dispatch?)
        return a.type().equals("SAVINGS");
    }
}
