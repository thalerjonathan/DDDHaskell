package at.fhv.se.banking.bdd.steps;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Account;
import at.fhv.se.banking.domain.model.account.GiroAccount;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.domain.model.account.SavingsAccount;
import at.fhv.se.banking.domain.model.account.exceptions.AccountException;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@ActiveProfiles("test")
@Transactional
public class TransferringSteps { //extends StepDefinitions {
    
    private final static String GIRO_TYPE = "GIRO";
    private final static String SAVINGS_TYPE = "SAVINGS";

    private AccountException whenException;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private AccountService accountService;

    @Given("a {word} account with Iban {string} and a balance of {double}")
    public void setupAccount(String accountType, String iban, double balance) throws AccountException {
        Account a = null;

        Customer c = new Customer(customerRepository.nextCustomerId(), "Test");
        this.customerRepository.add(c);

        if (accountType.equalsIgnoreCase(GIRO_TYPE)) {
            a = new GiroAccount(c.customerId(), new Iban(iban));
            a.deposit(balance, LocalDateTime.now());

            
        } else if (accountType.equalsIgnoreCase(SAVINGS_TYPE)) {
            a = new SavingsAccount(new CustomerId("1"), new Iban(iban));
            a.receiveFrom(a.iban(), balance, "Deposit", "Deposit", LocalDateTime.now());

        } else {
            fail("Invalid Account Type: " + accountType);
        }

        accountRepo.add(a);
    }

    @When("Transferring {double} from Iban {string} to Iban {string}")
    public void transfer(double amount, String sendingIban, String receivingIban) throws Exception {
        accountService.transferTransactional(amount, "Transfer", sendingIban, receivingIban);
    }

    @Then("There should be a balance of {double} in the Giro account with Iban {string}")
    public void expectBalance(double expectedBalance, String iban) {
        Account a = this.accountRepo.byIban(new Iban(iban)).get();
        assertEquals(expectedBalance, a.balance());
    }
}
