package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CustomerDetailsDTO {
    private CustomerDTO details;
    private final List<AccountDetailsDTO> accounts;

    public static Builder builder() {
        return new Builder();
    }

    public CustomerDTO details() {
        return this.details;
    }

    public List<AccountDetailsDTO> accounts() {
        return Collections.unmodifiableList(this.accounts);
    }

    private CustomerDetailsDTO() {
        this.accounts = new ArrayList<>();
    }

    public static class Builder {
        private final CustomerDetailsDTO instance;

        private Builder() {
            this.instance = new CustomerDetailsDTO();
        }

        public Builder withCustomer(CustomerDTO details) {
            this.instance.details = details;
            return this;
        }

        public Builder addAccount(AccountDetailsDTO ai) {
            this.instance.accounts.add(ai);
            return this;
        }

        public CustomerDetailsDTO build() {
            Objects.requireNonNull(this.instance.details, "customer must be set in CustomerInfoDTO");

            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
        result = prime * result + ((details == null) ? 0 : details.hashCode());
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
        CustomerDetailsDTO other = (CustomerDetailsDTO) obj;
        if (accounts == null) {
            if (other.accounts != null)
                return false;
        } else if (!accounts.equals(other.accounts))
            return false;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
        return true;
    }
}
