package at.fhv.se.banking.application.dto;

import java.util.Objects;

public class CustomerDTO {
    private String name;

    public static Builder create() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    private CustomerDTO() {
    }

    public static class Builder {
        private CustomerDTO instance;

        private Builder() {
            this.instance = new CustomerDTO();
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        }

        public CustomerDTO build() {
            Objects.requireNonNull(this.instance.name, "name must be set in CustomerDTO");

            return this.instance;
        }
    }
}
