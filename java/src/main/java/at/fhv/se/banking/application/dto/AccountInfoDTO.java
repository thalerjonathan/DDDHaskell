package at.fhv.se.banking.application.dto;

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

        public Builder withIban(String iban) {
            this.instance.iban = iban;
            return this;
        }

        public Builder withType(String type) {
            this.instance.type = type;
            return this;
        }

        public Builder withBalance(double balance) {
            this.instance.balance = balance;
            return this;
        }

        public AccountInfoDTO build() {
            return this.instance;
        }
    }
}
