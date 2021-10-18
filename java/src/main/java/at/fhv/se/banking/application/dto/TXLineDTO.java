package at.fhv.se.banking.application.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import at.fhv.se.banking.domain.model.Iban;

public final class TXLineDTO {
    private String iban;
    private String name;
    private String reference;
    private double amount;
    private LocalDateTime time;

    public static Builder builder() {
        return new Builder();
    }

    public String iban() {
        return this.iban;
    }

    public String name() {
        return this.name;
    }

    public double amount() {
        return this.amount;
    }

    public String reference() {
        return this.reference;
    }

    public LocalDateTime time() {
        return time;
    }

    private TXLineDTO() {
    }

    public static class Builder {
        private final TXLineDTO instance;

        private Builder() {
            this.instance = new TXLineDTO();
        }

        public Builder withIban(Iban iban) {
            this.instance.iban = iban.toString();
            return this;
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        }

        public Builder ofAmount(double amount) {
            this.instance.amount = amount;
            return this;
        }

        public Builder atTime(LocalDateTime time) {
            this.instance.time = time;
            return this;
        }

        public Builder withReference(String reference) {
            this.instance.reference = reference;
            return this;
        }

        public TXLineDTO build() {
            Objects.requireNonNull(this.instance.iban, "iban must be set in TXLineDTO");
            Objects.requireNonNull(this.instance.name, "name must be set in TXLineDTO");
            Objects.requireNonNull(this.instance.time, "time must be set in TXLineDTO");
            Objects.requireNonNull(this.instance.reference, "reference must be set in TXLineDTO");

            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((iban == null) ? 0 : iban.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
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
        TXLineDTO other = (TXLineDTO) obj;
        if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
            return false;
        if (iban == null) {
            if (other.iban != null)
                return false;
        } else if (!iban.equals(other.iban))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reference == null) {
            if (other.reference != null)
                return false;
        } else if (!reference.equals(other.reference))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }
}
