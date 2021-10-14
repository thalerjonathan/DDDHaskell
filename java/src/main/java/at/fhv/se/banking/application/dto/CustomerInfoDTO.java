package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomerInfoDTO {
    private CustomerDTO customer;
    private final List<AccountInfoDTO> accountInfos;

    public static Builder create() {
        return new Builder();
    }

    public CustomerDTO customer() {
        return this.customer;
    }

    public List<AccountInfoDTO> accountInfos() {
        return Collections.unmodifiableList(this.accountInfos);
    }

    private CustomerInfoDTO() {
        this.accountInfos = new ArrayList<>();
    }

    public static class Builder {
        private final CustomerInfoDTO instance;

        private Builder() {
            this.instance = new CustomerInfoDTO();
        }

        public Builder withCustomer(CustomerDTO customer) {
            this.instance.customer = customer;
            return this;
        }

        public Builder addAccountInfo(AccountInfoDTO ai) {
            this.instance.accountInfos.add(ai);
            return this;
        }

        public CustomerInfoDTO build() {
            Objects.requireNonNull(this.instance.customer, "customer must be set in CustomerInfoDTO");

            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountInfos == null) ? 0 : accountInfos.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
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
        CustomerInfoDTO other = (CustomerInfoDTO) obj;
        if (accountInfos == null) {
            if (other.accountInfos != null)
                return false;
        } else if (!accountInfos.equals(other.accountInfos))
            return false;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        return true;
    }
}
