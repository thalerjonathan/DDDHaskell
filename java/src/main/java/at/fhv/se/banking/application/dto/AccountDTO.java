package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AccountDTO {
    private AccountDetailsDTO details;
    private final List<TXLineDTO> txLines;
    
    public static Builder create() {
        return new Builder();
    }

    public AccountDetailsDTO details() {
        return this.details;
    }

    public List<TXLineDTO> txLines() {
        return Collections.unmodifiableList(this.txLines);
    }

    private AccountDTO() {
        this.txLines = new ArrayList<>();
    }

    public static class Builder {
        private final AccountDTO instance;

        private Builder() {
            this.instance = new AccountDTO();
        }

        public Builder withDeails(AccountDetailsDTO details) {
            this.instance.details = details;
            return this;
        }

        public Builder addTXLine(TXLineDTO txLine) {
            this.instance.txLines.add(txLine);
            return this;
        }

        public AccountDTO build() {
            Objects.requireNonNull(this.instance.details, "details must be set in AccountDTO");

            return this.instance;
        }   
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + ((txLines == null) ? 0 : txLines.hashCode());
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
        AccountDTO other = (AccountDTO) obj;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
        if (txLines == null) {
            if (other.txLines != null)
                return false;
        } else if (!txLines.equals(other.txLines))
            return false;
        return true;
    }
}
