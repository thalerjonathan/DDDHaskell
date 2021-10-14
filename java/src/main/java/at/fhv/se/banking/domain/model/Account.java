package at.fhv.se.banking.domain.model;

public class Account {
    private AccountId accountId;
    private Iban iban;
    private double balance;
    private AccountType type;

    public Account(AccountId accountId, Iban iban, AccountType type, double balance) {
        this.accountId = accountId;
        this.iban = iban;
        this.balance = balance;
        this.type = type;
    }

    public AccountId accountId() {
        return accountId;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
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
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        return true;
    }
}
