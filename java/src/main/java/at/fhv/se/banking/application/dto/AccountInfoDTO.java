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

    public String iban() {
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
}
