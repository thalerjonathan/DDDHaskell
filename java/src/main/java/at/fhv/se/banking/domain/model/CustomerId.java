package at.fhv.se.banking.domain.model;

public final class CustomerId {
    private String id;

    public CustomerId(String id) {
        this.id = id;
    }
    
    public String id() {
        return this.id;
    }

    @SuppressWarnings("unused")
    private CustomerId() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        CustomerId other = (CustomerId) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
