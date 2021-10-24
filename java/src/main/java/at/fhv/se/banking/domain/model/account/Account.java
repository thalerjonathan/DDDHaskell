package at.fhv.se.banking.domain.model.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;

public abstract class Account {
    // required by Hibernate
    @SuppressWarnings("unused")
    private Long id;
    @SuppressWarnings("unused")
    private int version;

    private CustomerId owner;
    private Iban iban;
    protected Set<TXLine> txLines;
    
    public Account(CustomerId owner, Iban iban) {
        this.owner = owner;
        this.iban = iban;
        this.txLines = new LinkedHashSet<>();
    }

    public CustomerId owner() {
        return this.owner;
    }

    public Iban iban() {
        return iban;
    }

    public abstract String type();

    public List<TXLine> transactions() {
        return Collections.unmodifiableList(new ArrayList<TXLine>(this.txLines));
    }

    public double balance() {
        return this.txLines
            .stream()
            .mapToDouble(tx -> tx.amount())
            .sum();
    }

    public abstract void deposit(double amount, LocalDateTime time) throws AccountException;
    public abstract void withdraw(double amount, LocalDateTime time) throws AccountException;
    public abstract void receiveFrom(Iban from, double amount, String name, String reference, LocalDateTime time);
    public abstract void transferTo(Iban to, double amount, String name, String reference, LocalDateTime time) throws AccountException;

    // required by Hibernate
    protected Account() {
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
