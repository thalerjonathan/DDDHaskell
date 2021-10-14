package at.fhv.se.banking.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AccountDTO {
    private AccountInfoDTO accountInfo;
    private final List<TXLineDTO> txLines;
    
    public static Builder create() {
        return new Builder();
    }

    public AccountInfoDTO getInfo() {
        return this.accountInfo;
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

        public Builder withInfo(AccountInfoDTO info) {
            this.instance.accountInfo = info;
            return this;
        }

        public Builder addTXLine(TXLineDTO txLine) {
            this.instance.txLines.add(txLine);
            return this;
        }

        public AccountDTO build() {
            Objects.requireNonNull(this.instance.accountInfo, "accountInfo must be set in AccountDTO");

            return this.instance;
        }   
    }
}
