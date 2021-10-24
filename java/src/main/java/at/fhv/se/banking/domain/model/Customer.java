package at.fhv.se.banking.domain.model;

public final class Customer {
    // required by Hibernate
    @SuppressWarnings("unused")
    private Long id;

    private CustomerId customerId;
    private String name;

    public Customer(CustomerId id, String name) {
        this.customerId = id;
        this.name = name;
    }

    public CustomerId customerId() {
        return this.customerId;
    }

    public String name() {
        return this.name;
    }

    // required by Hibernate
    @SuppressWarnings("unused")
    private Customer() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
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
        Customer other = (Customer) obj;
        if (customerId == null) {
            if (other.customerId != null)
                return false;
        } else if (!customerId.equals(other.customerId))
            return false;
        return true;
    }
}
