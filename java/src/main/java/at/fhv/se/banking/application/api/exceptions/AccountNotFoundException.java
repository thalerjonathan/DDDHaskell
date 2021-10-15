package at.fhv.se.banking.application.api.exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
