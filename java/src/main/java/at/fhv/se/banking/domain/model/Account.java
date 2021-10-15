package at.fhv.se.banking.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private double balance;
    private Iban iban;
    private AccountType type;
    private List<TXLine> txLines;
    
    public Account(Iban iban, AccountType type, double balance) {
        this.iban = iban;
        this.balance = balance;
        this.type = type;
        this.txLines = new ArrayList<>();
    }

    public Iban iban() {
        return iban;
    }

    public double balance() {
        return balance;
    }

    public AccountType type() {
        return this.type;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    // TODO: do we really require in proper Domain Model?
    public List<TXLine> txLines() {
        return Collections.unmodifiableList(this.txLines);
    }

    // TODO: remove in proper Domain Model
    public void addTXLine(TXLine txLine) {
        this.txLines.add(txLine);
    }

    public void transferTo(Iban to, double amount, String name, String reference) {
        this.balance -= amount;
        this.txLines.add(new TXLine(to, amount, name, reference, LocalDateTime.now()));
    }

    public void receiveFrom(Iban from, double amount, String name, String reference) {
        this.balance += amount;
        this.txLines.add(new TXLine(from, amount, name, reference, LocalDateTime.now()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iban == null) ? 0 : iban.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account other = (Account) obj;
        if (iban == null) {
            if (other.iban != null)
                return false;
        } else if (!iban.equals(other.iban))
            return false;
        return true;
    }
}
