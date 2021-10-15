package at.fhv.se.banking.application.api.exceptions;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException() {}

    public CustomerNotFoundException(String msg) {
        super(msg);
    }
    
}
