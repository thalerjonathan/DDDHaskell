package at.fhv.se.banking.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private CustomerId owner;
    private Iban iban;
    private AccountType type;
    private List<TXLine> txLines;
    
    public Account(CustomerId owner, Iban iban, AccountType type) {
        this.owner = owner;
        this.iban = iban;
        this.type = type;
        this.txLines = new ArrayList<>();
    }

    public CustomerId owner() {
        return this.owner;
    }

    public Iban iban() {
        return iban;
    }

    public AccountType type() {
        return this.type;
    }

    public List<TXLine> transactions() {
        return Collections.unmodifiableList(this.txLines);
    }

    public double balance() {
        return this.txLines
            .stream()
            .mapToDouble(tx -> tx.amount())
            .sum();
    }

    public void deposit(double amount) {
        this.txLines.add(new TXLine(this.iban, amount, "Deposit", "Deposit", LocalDateTime.now()));
    }

    public void withdraw(double amount) {
        this.txLines.add(new TXLine(this.iban, -amount, "Deposit", "Deposit", LocalDateTime.now()));
    }

    public void transferTo(Iban to, double amount, String name, String reference, LocalDateTime time) {
        this.txLines.add(new TXLine(to, -amount, name, reference, time));
    }

    public void receiveFrom(Iban from, double amount, String name, String reference, LocalDateTime time) {
        this.txLines.add(new TXLine(from, amount, name, reference, time));
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
