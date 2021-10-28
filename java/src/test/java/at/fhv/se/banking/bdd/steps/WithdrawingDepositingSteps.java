package at.fhv.se.banking.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.infrastructure.AccountData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WithdrawingDepositingSteps {
    
    private final static String GIRO_TYPE = "GIRO";
    private final static String SAVINGS_TYPE = "SAVINGS";

    private AccountException whenException;

    private Account a;

    @Given("my {word} account has a balance of {double}")
    public void setupAccountDepositing(String accountType, double balance) {
        if (accountType.equalsIgnoreCase(GIRO_TYPE)) {
            this.a = AccountData.createGiroWithBalance(balance);
        } else if (accountType.equalsIgnoreCase(SAVINGS_TYPE)) {
            this.a = AccountData.createSavingsWithBalance(balance);
        }
    }

    @When("I deposit {double} into my account")
    public void deposit(double amount) {
        try {
            this.a.deposit(amount, LocalDateTime.now());
        } catch (AccountException e) {
            whenException = e;
        }
    }

    @When("I withdraw {double} from my account")
    public void withdraw(double amount) {
        try {
            this.a.withdraw(amount, LocalDateTime.now());
        } catch (AccountException e) {
            whenException = e;
        }
    }

    @Then("I should have a balance of {double} in my account") 
    public void expectDepositingBalance(double expectedBalance) {
        assertEquals(expectedBalance, a.balance(), 0.001);
    }

    @Then("I expect the error {string}") 
    public void expectError(String expectedError) {
        assertNotNull(whenException);
        assertEquals(expectedError, whenException.getMessage());
    }
}
