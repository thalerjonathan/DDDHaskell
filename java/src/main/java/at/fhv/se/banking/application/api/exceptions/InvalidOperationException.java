package at.fhv.se.banking.application.api.exceptions;

public class InvalidOperationException extends Exception {
    
    public InvalidOperationException(String msg) {
        super(msg);
    }
}
