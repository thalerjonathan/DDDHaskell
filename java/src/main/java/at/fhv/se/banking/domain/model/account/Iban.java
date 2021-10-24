package at.fhv.se.banking.domain.model.account;

public final class Iban {
    private String iban;

    public Iban(String iban) {
        this.iban = iban;
    }

    @Override
    public String toString() {
        return iban;
    }

    @SuppressWarnings("unused")
    private Iban() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iban == null) ? 0 : iban.hashCode());
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
        Iban other = (Iban) obj;
        if (iban == null) {
            if (other.iban != null)
                return false;
        } else if (!iban.equals(other.iban))
            return false;
        return true;
    }
}
