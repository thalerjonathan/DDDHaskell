package at.fhv.se.banking.domain.model.account;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public class GiroAccount extends Account {

    private final static String ACCOUNT_TYPE = "GIRO";
    private final static double OVERDRAFT_LIMIT = -1000;

    public GiroAccount(CustomerId owner, Iban iban) {
        super(owner, iban);
    }
 
    @Override
    public String type() {
        return ACCOUNT_TYPE;
    }

    @Override
    public void deposit(double amount, LocalDateTime time) {
        this.txLines.add(new TXLine(this.iban(), amount, "Deposit", "Deposit", time));
    }

    @Override
    public void withdraw(double amount, LocalDateTime time) throws AccountException {
        if (this.balance() - amount < OVERDRAFT_LIMIT) {
            throw new AccountException("Cannot overdraw Giro account by more than " + OVERDRAFT_LIMIT + "!");
        }

        this.txLines.add(new TXLine(this.iban(), -amount, "Withdraw", "Withdraw", time));
    }

    @Override
    public void receiveFrom(Iban from, double amount, String name, String reference, LocalDateTime time) {
        this.txLines.add(new TXLine(from, amount, name, reference, time));
    }

    @Override
    public void transferTo(Iban to, double amount, String name, String reference, LocalDateTime time) {
        this.txLines.add(new TXLine(to, -amount, name, reference, time));
    }
}
