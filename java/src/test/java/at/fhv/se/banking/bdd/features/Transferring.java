package at.fhv.se.banking.bdd.features;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class Transferring {
    
    private double current;
    private double savings;
    private boolean insufficientFunds;

    @Given("my Current account has a balance of {double}")
    public void setupCurrentAccount(double balance) {
        this.current = balance;
    }

    @Given("my Savings account has a balance of {double}")
    public void setupSavingsAccount(double balance) {
        this.savings = balance;
    }

    @When("I transfer {double} from my Current account to my Savings account")
    public void transferFromCurentToSavings(double amount) {
        if (this.current >= amount) {
            this.current -= amount;
            this.savings += amount;
        } else {
            insufficientFunds = true;
        }
    }

    @Then("I should receive an 'insufficient funds' error") 
    public void expectInsufficientFunds() {
        assertTrue(this.insufficientFunds);
    }

    @Then("I should have {double} in my Current account") 
    public void expectBalanceChangedInCurrentAccount(double expectedBalance) {
        assertEquals(expectedBalance, this.current);
    }

    @Then("I should have {double} in my Savings account") 
    public void expectBalanceChangedInSavingsAccount(double expectedBalance) {
        assertEquals(expectedBalance, this.savings);
    }
}
