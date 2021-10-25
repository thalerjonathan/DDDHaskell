package at.fhv.se.banking.domain.events;

import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Iban;

public class TransferFailed implements DomainEvent {
    private String error;
    private double amount;
    private String reference;
    private CustomerId sendingCustomer;
    private CustomerId receivingCustomer;
    private Iban sendingAccount;
    private Iban receivingAccount;

    public TransferFailed(String error, TransferSent sent) {
        this.error = error;
        this.amount = sent.amount();
        this.reference = sent.reference();
        this.sendingCustomer = sent.sendingCustomer();
        this.receivingCustomer = sent.receivingCustomer();
        this.sendingAccount = sent.sendingAccount();
        this.receivingAccount = sent.receivingAccount();   
    }

    public TransferFailed(String error, double amount, String reference, 
            CustomerId sendingCustomer, CustomerId receivingCustomer,
            Iban sendingAccount, Iban receivingAccount) {
        this.error = error;
        this.amount = amount;
        this.reference = reference;
        this.sendingCustomer = sendingCustomer;
        this.receivingCustomer = receivingCustomer;
        this.sendingAccount = sendingAccount;
        this.receivingAccount = receivingAccount;   
    }

    public String error() {
        return this.error;
    }

    public double amount() {
        return amount;
    }

    public String reference() {
        return reference;
    }

    public CustomerId sendingCustomer() {
        return sendingCustomer;
    }

    public CustomerId receivingCustomer() {
        return receivingCustomer;
    }

    public Iban sendingAccount() {
        return sendingAccount;
    }

    public Iban receivingAccount() {
        return receivingAccount;
    }

    // NOTE: need for JSON deserialisation
    @SuppressWarnings("unused")
    private TransferFailed() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((receivingAccount == null) ? 0 : receivingAccount.hashCode());
        result = prime * result + ((receivingCustomer == null) ? 0 : receivingCustomer.hashCode());
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((sendingAccount == null) ? 0 : sendingAccount.hashCode());
        result = prime * result + ((sendingCustomer == null) ? 0 : sendingCustomer.hashCode());
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
        TransferFailed other = (TransferFailed) obj;
        if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
            return false;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        if (receivingAccount == null) {
            if (other.receivingAccount != null)
                return false;
        } else if (!receivingAccount.equals(other.receivingAccount))
            return false;
        if (receivingCustomer == null) {
            if (other.receivingCustomer != null)
                return false;
        } else if (!receivingCustomer.equals(other.receivingCustomer))
            return false;
        if (reference == null) {
            if (other.reference != null)
                return false;
        } else if (!reference.equals(other.reference))
            return false;
        if (sendingAccount == null) {
            if (other.sendingAccount != null)
                return false;
        } else if (!sendingAccount.equals(other.sendingAccount))
            return false;
        if (sendingCustomer == null) {
            if (other.sendingCustomer != null)
                return false;
        } else if (!sendingCustomer.equals(other.sendingCustomer))
            return false;
        return true;
    }
}
