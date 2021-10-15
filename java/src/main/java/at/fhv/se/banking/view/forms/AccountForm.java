package at.fhv.se.banking.view.forms;

public class AccountForm {
    private double amount;
    private String customerId;
    private String customerName;
    private String iban;

    // required by spring/thymeleaf
    public AccountForm() {
    }

    public AccountForm(String customerId, String customerName, String iban) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.iban = iban;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
}
