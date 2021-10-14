package at.fhv.se.banking.application.dto;

import java.util.Objects;

import at.fhv.se.banking.domain.model.CustomerId;

public class CustomerDTO {
    private String id;
    private String name;

    public static Builder create() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    public String id() {
        return this.id;
    }

    private CustomerDTO() {
    }

    public static class Builder {
        private CustomerDTO instance;

        private Builder() {
            this.instance = new CustomerDTO();
        }

        public Builder withId(CustomerId id) {
            this.instance.id = id.id();
            return this;
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        } 

        public CustomerDTO build() {
            Objects.requireNonNull(this.instance.name, "name must be set in CustomerDTO");
            Objects.requireNonNull(this.instance.id, "id must be set in CustomerDTO");

            return this.instance;
        }
    }


}
