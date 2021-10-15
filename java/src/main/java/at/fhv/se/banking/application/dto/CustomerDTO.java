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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        CustomerDTO other = (CustomerDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
