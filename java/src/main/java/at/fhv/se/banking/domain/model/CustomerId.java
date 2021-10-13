package at.fhv.se.banking.domain.model;

public class CustomerId {
    private int id;

    public CustomerId(int id) {
        this.id = id;
    }
    
    public int id() {
        return this.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        if (id != other.id)
            return false;
        return true;
    }
}
