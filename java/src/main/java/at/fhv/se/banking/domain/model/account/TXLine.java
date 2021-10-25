package at.fhv.se.banking.domain.model.account;

import java.time.LocalDateTime;

public final class TXLine {
    // required by Hibernate
    @SuppressWarnings("unused")
    private Long id;
    @SuppressWarnings("unused")
    private Iban account;

    private double amount;
    private Iban iban;
    private String name;
    private String reference;
    private LocalDateTime time; // using Date because Hibernate mapping f*** up when using LocalDateTime

    public TXLine(Account account, Iban iban, double amount, String name, String reference, LocalDateTime time) {
        this.account = account.iban();
        this.amount = amount;
        this.iban = iban;
        this.name = name;
        this.reference = reference;
        this.time = time;
    }

    public double amount() {
        return amount;
    }

    public Iban iban() {
        return iban;
    }

    public String name() {
        return name;
    }

    public String reference() {
        return reference;
    }

    public LocalDateTime time() {
        return this.time;
    }

    // required by Hibernate
    @SuppressWarnings("unused")
    private TXLine() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((iban == null) ? 0 : iban.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
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
        TXLine other = (TXLine) obj;
        if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
            return false;
        if (iban == null) {
            if (other.iban != null)
                return false;
        } else if (!iban.equals(other.iban))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reference == null) {
            if (other.reference != null)
                return false;
        } else if (!reference.equals(other.reference))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }
}
