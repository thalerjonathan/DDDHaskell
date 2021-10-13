package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerInfoDTO {
    private CustomerDTO customer;
    private List<AccountInfoDTO> accountInfos;

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
        private CustomerInfoDTO instance;

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
            return this.instance;
        }
    }
}
