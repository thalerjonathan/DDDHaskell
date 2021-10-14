package at.fhv.se.banking.view.forms;

public class AccountForm {
    private double amount;
    private String customer;
    private String iban;

    // required by spring/thymeleaf
    public AccountForm() {
    }

    public AccountForm(String customer, String iban) {
        this.customer = customer;
        this.iban = iban;
    }

    public AccountForm(double amount, String customer, String iban) {
        this.amount = amount;
        this.customer = customer;
        this.iban = iban;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
}
