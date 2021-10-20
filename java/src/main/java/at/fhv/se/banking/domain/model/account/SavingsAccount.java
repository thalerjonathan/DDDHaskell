package at.fhv.se.banking.domain.model.account;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public class SavingsAccount extends Account {

    private final static String ACCOUNT_TYPE = "SAVINGS";

    public SavingsAccount(CustomerId owner, Iban iban) {
        super(owner, iban);
    }

    @Override
    public String type() {
        return ACCOUNT_TYPE;
    }

    @Override
    public void deposit(double amount, LocalDateTime time) throws AccountException {
        throw new AccountException("Cannot deposit money directly into Savings Account! Use transfer of money from a Giro Account of the same customer.");
    }

    @Override
    public void withdraw(double amount, LocalDateTime time) throws AccountException {
        throw new AccountException("Cannot withdraw money directly from Savings Account! Use transfer of money into a Giro Account of the same customer.");
    }

    @Override
    public void receiveFrom(Iban from, double amount, String name, String reference, LocalDateTime time) {
        this.txLines.add(new TXLine(from, amount, name, reference, time));
    }

    @Override
    public void transferTo(Iban to, double amount, String name, String reference, LocalDateTime time) throws AccountException {
        double balance = this.balance();
        if (balance - amount < 0) {
            throw new AccountException("Cannot overdraw Savings account!");
        }

        this.txLines.add(new TXLine(to, -amount, name, reference, time));
    }
}
