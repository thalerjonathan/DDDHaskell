package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CustomerDTO {
    private CustomerDetailsDTO details;
    private final List<AccountDetailsDTO> accounts;

    public static Builder builder() {
        return new Builder();
    }

    public CustomerDetailsDTO details() {
        return this.details;
    }

    public List<AccountDetailsDTO> accounts() {
        return Collections.unmodifiableList(this.accounts);
    }

    private CustomerDTO() {
        this.accounts = new ArrayList<>();
    }

    public static class Builder {
        private final CustomerDTO instance;

        private Builder() {
            this.instance = new CustomerDTO();
        }

        public Builder withCustomer(CustomerDetailsDTO details) {
            this.instance.details = details;
            return this;
        }

        public Builder addAccount(AccountDetailsDTO ai) {
            this.instance.accounts.add(ai);
            return this;
        }

        public CustomerDTO build() {
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
        CustomerDTO other = (CustomerDTO) obj;
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
