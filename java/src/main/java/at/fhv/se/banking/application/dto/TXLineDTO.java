package at.fhv.se.banking.application.dto;

import java.util.Objects;

import at.fhv.se.banking.domain.model.Iban;

public class TXLineDTO {
    private String iban;
    private String name;
    private double amount;

    public static Builder create() {
        return new Builder();
    }

    public String iban() {
        return this.iban;
    }

    public String name() {
        return this.name;
    }

    public double amount() {
        return this.amount;
    }

    private TXLineDTO() {
    }

    public static class Builder {
        private final TXLineDTO instance;

        private Builder() {
            this.instance = new TXLineDTO();
        }

        public Builder fromIban(Iban iban) {
            this.instance.iban = iban.toString();
            return this;
        }

        public Builder fromName(String name) {
            this.instance.name = name;
            return this;
        }

        public Builder ofAmount(double amount) {
            this.instance.amount = amount;
            return this;
        }

        public TXLineDTO build() {
            Objects.requireNonNull(this.instance.iban, "iban must be set in TXLineDTO");
            Objects.requireNonNull(this.instance.name, "name must be set in TXLineDTO");

            return this.instance;
        }
    }
}
