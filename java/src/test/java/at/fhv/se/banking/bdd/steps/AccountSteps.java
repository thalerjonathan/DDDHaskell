package at.fhv.se.banking.bdd.steps;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.bdd.runner.ScenarioTXBoundary;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.SavingsAccount;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@ActiveProfiles("test")
@CucumberContextConfiguration
@SpringBootTest
public class AccountSteps extends ScenarioTXBoundary {
    
    private final static String GIRO_TYPE = "GIRO";
    private final static String SAVINGS_TYPE = "SAVINGS";

    private final static String MY_IBAN = "AT12 12345 01234567890";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private AccountService accountService;

    private Exception whenException;
    
    @Before
    public void beforeScenario(){       
        this.beginTX();
    }

    @After
    public void afterScenario(){
        this.rollbackTX();
    }

    @Given("my {word} account has a balance of {double}")
    public void setupMyAccount(String accountType, double balance) throws AccountException {
        this.setupAccount(accountType, MY_IBAN, balance);
    }

    @Given("a {word} account with Iban {string} and a balance of {double}")
    public void setupAccount(String accountType, String iban, double balance) throws AccountException {
        CustomerId cId = customerRepository.nextCustomerId();
        Customer c = new Customer(cId, "Test Customer " + cId);
        this.customerRepository.add(c);

        setupAccountForCustomer(cId, accountType, iban, balance);
    }

    @Given("a {word} account of the same customer with Iban {string} and a balance of {double}")
    public void setupAccountSameCustomer(String accountType, String iban, double balance) throws AccountException {
        CustomerId cId = customerRepository.all().get(0).customerId();

        setupAccountForCustomer(cId, accountType, iban, balance);
    }

    private void setupAccountForCustomer(CustomerId cId, String accountType, String iban, double balance) throws AccountException {
        Account a = null;

        if (accountType.equalsIgnoreCase(GIRO_TYPE)) {
            a = new GiroAccount(cId, new Iban(iban));
            a.deposit(balance, LocalDateTime.now());

        } else if (accountType.equalsIgnoreCase(SAVINGS_TYPE)) {
            a = new SavingsAccount(cId, new Iban(iban));
            a.receiveFrom(a.iban(), balance, "Deposit", "Deposit", LocalDateTime.now());

        } else {
            fail("Invalid Account Type: " + accountType);
        }

        accountRepo.add(a);
    }

    @When("Transferring {double} from Iban {string} to Iban {string}")
    public void transfer(double amount, String sendingIban, String receivingIban) {
        try {
            accountService.transferTransactional(amount, "Transfer", sendingIban, receivingIban);
        } catch (AccountNotFoundException | CustomerNotFoundException | InvalidOperationException e) {
            whenException = e;
        }
    }

    @When("I deposit {double} into my account")
    public void deposit(double amount) {
        try {
            accountService.deposit(amount, MY_IBAN);
        } catch (InvalidOperationException | AccountNotFoundException e) {
            whenException = e;
        }
    }

    @When("I withdraw {double} from my account")
    public void withdraw(double amount) {
        try {
            accountService.withdraw(amount, MY_IBAN);
        } catch (InvalidOperationException | AccountNotFoundException e) {
            whenException = e;
        }
    }

    @Then("There should be a balance of {double} in the account with Iban {string}")
    public void expectBalance(double expectedBalance, String iban) {
        Account a = this.accountRepo.byIban(new Iban(iban)).get();
        assertEquals(expectedBalance, a.balance(), 0.001);
    }
   

    @Then("I should have a balance of {double} in my account") 
    public void expectMyBalance(double expectedBalance) {
        expectBalance(expectedBalance, MY_IBAN);
    }

    @Then("I expect the error {string}") 
    public void expectError(String expectedError) {
        assertNotNull(whenException);
        assertEquals(expectedError, whenException.getMessage());
    }
}
