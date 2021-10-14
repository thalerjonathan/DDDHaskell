package at.fhv.se.banking.application.dto;

import java.util.Objects;

import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.Iban;

public class AccountInfoDTO {
    private String iban;
    private double balance;
    private String type;

    public static Builder create() {
        return new Builder();
    }

    public String getIban() {
        return this.iban;
    }

    public String type() {
        return this.type;
    }

    public double balance() {
        return this.balance;
    }

    private AccountInfoDTO() {
    }

    public static class Builder {
        private AccountInfoDTO instance;

        private Builder() {
            this.instance = new AccountInfoDTO();
        }

        public Builder withIban(Iban iban) {
            this.instance.iban = iban.toString();
            return this;
        }

        public Builder withType(AccountType type) {
            this.instance.type = type.name();
            return this;
        }

        public Builder withBalance(double balance) {
            this.instance.balance = balance;
            return this;
        }

        public AccountInfoDTO build() {
            Objects.requireNonNull(this.instance.type, "type must be set in AccountDTO");

            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((iban == null) ? 0 : iban.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        AccountInfoDTO other = (AccountInfoDTO) obj;
        if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
            return false;
        if (iban == null) {
            if (other.iban != null)
                return false;
        } else if (!iban.equals(other.iban))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
