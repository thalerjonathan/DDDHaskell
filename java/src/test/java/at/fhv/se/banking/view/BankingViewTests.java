package at.fhv.se.banking.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlNumberInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.utils.TestingUtils;

@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
public class BankingViewTests {
    
    private WebClient webClient;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    void setup(WebApplicationContext context) {
        this.webClient = MockMvcWebClientBuilder
                            .webAppContextSetup(context)
                            .build();
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
    }

    @AfterEach
    void teardown() {
        this.webClient.close();
    }

    @Test
    public void given_customers_when_welcomepage_displaycustomers() throws Exception {
        // given
        List<CustomerDetailsDTO> customers = Arrays.asList(
            CustomerDetailsDTO.builder()
                .withName("Alice")
                .withId(new CustomerId("1"))
                .build(),
            CustomerDetailsDTO.builder()
                .withName("Bob")
                .withId(new CustomerId("2"))
                .build()
        );
        Mockito.when(customerService.listAll()).thenReturn(customers);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");

        // then
        assertEquals("Banking", page.getTitleText());

        final List<HtmlAnchor> customerButtons = page.getByXPath("//ul//a");
        TestingUtils.assertEqualsCollections(customerButtons, customers, (cb, dto) -> {
            String href = cb.getHrefAttribute();

            assertEquals("/customer?id=" + dto.id(), href);
            assertEquals(dto.name(), cb.asNormalizedText());
        });
    }

    @Test
    public void given_customer_when_view_displaydetailsandaccounts() throws Exception {
        // given
        String customerName = "Jonathan";
        CustomerId customerId = new CustomerId("1");
        CustomerDTO customerInfo = CustomerDTO.builder()
            .withCustomer(CustomerDetailsDTO.builder()
                .withName(customerName)
                .withId(customerId)
                .build())
            .addAccount(AccountDetailsDTO.builder()
                .withIban(new Iban("AT12 3456 7890 1234"))
                .withType("GIRO")
                .withBalance(1234.5).build())
            .addAccount(AccountDetailsDTO.builder()
                .withIban(new Iban("AT98 7654 3210 9876"))
                .withType("SAVINGS")
                .withBalance(1234.5).build())
            .build();

        Mockito.when(customerService.detailsFor(customerId.id())).thenReturn(customerInfo);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/customer?id=" + customerId.id());

        // then
        final HtmlHeading1 heading = (HtmlHeading1) page.getByXPath("//h1").get(0);
        final List<HtmlListItem> items = page.getByXPath("//ul//li");

        assertEquals("Banking", page.getTitleText());
        assertEquals(customerName, heading.getTextContent());

        TestingUtils.assertEqualsCollections(items, customerInfo.accounts(), (li, dto) -> {
            final HtmlAnchor a = (HtmlAnchor) li.getFirstByXPath(".//a");
            final HtmlSpan span = (HtmlSpan) li.getFirstByXPath(".//span");

            String href = a.getHrefAttribute();
            String expectedHref = "/account?iban=" +
                dto.getIban().replace(" ", "%20") + "&id=" + 
                customerId.id() + "&name=" + 
                customerName;

            assertEquals(expectedHref, href);
            assertEquals(dto.getIban().toString() + " (" + dto.type() + ")", a.asNormalizedText());
            assertEquals("" + dto.balance(), span.asNormalizedText());
        });
    }

    @Test
    public void given_customernotfound_when_view_displaydetailsandaccounts_thenerrorpage() throws Exception {
        // given
        String expectedErrorMessage = "Error: Customer not found!";
        CustomerId customerId = new CustomerId("1");
        Mockito.when(customerService.detailsFor(customerId.id())).thenThrow(new CustomerNotFoundException());

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/customer?id=" + customerId.id());

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) page.getByXPath("//h1").get(0);

        assertEquals("Banking", page.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    @Test
    public void given_account_when_view_displayinfoandalltransactions() throws Exception {
        // given
        String customerId = "1";
        String customerName = "Jonathan";
        Iban iban = new Iban("AT12 3456 7890 1234");

        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(1234.5)
                .build())
            .addTXLine(TXLineDTO.builder()
                .withIban(new Iban("AT98 7654 3210 9876"))
                .withName("Hans Huber")
                .ofAmount(-100.0)
                .atTime(LocalDateTime.now())
                .withReference("Lunch")
                .build())
            .addTXLine(TXLineDTO.builder()
                .withIban(new Iban("AT11 2222 3333 4444"))
                .withName("Max Mustermann")
                .ofAmount(200.0)
                .atTime(LocalDateTime.now())
                .withReference("Rent")
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);

        // when
        final HtmlPage page = this.fetchAccountPage(iban.toString(), customerId, customerName);

        // then
        final HtmlHeading1 customerHeading = (HtmlHeading1) page.getByXPath("//h1").get(0);
        final HtmlHeading2 accountIbanHeading = (HtmlHeading2) page.getByXPath("//h2").get(0);
        final HtmlHeading3 accountBalanceHeading = (HtmlHeading3) page.getByXPath("//h3").get(0);

        final List<HtmlListItem> txItems = page.getByXPath("//ul//li");
    
        assertEquals("Banking", page.getTitleText());

        assertEquals(customerName, customerHeading.getTextContent());
        assertEquals(iban.toString() + " (" + accountInfo.details().type() + ")", accountIbanHeading.getTextContent());
        assertEquals("Balance: " + accountInfo.details().balance(), accountBalanceHeading.getTextContent());

        TestingUtils.assertEqualsCollections(txItems, accountInfo.txLines(), (li, txLine) -> {
            final List<HtmlParagraph> ps = li.getByXPath("p");

            assertEquals("Amount: " + txLine.amount(), ps.get(0).getTextContent());
            assertEquals("Name: " + txLine.name(), ps.get(1).getTextContent());
            assertEquals("Iban: " + txLine.iban(), ps.get(2).getTextContent());
            assertEquals("Reference: " + txLine.reference(), ps.get(3).getTextContent());
        });
    }

    @Test
    public void given_noaccount_when_viewdisplayinfoandalltransactions_then_showerrorpage() throws Exception {
        // given
        String expectedErrorMessage = "Error: Account not found!";
        String customerName = "Jonathan";
        CustomerId customerId = new CustomerId("1");
        Iban iban = new Iban("AT12 3456 7890 1234");
        Mockito.when(accountService.accountByIban(iban.toString())).thenThrow(new AccountNotFoundException(""));

        // when
        final HtmlPage page = this.fetchAccountPage(iban.toString(), customerId.id(), customerName);

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) page.getByXPath("//h1").get(0);

        assertEquals("Banking", page.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    @Test
    public void given_account_when_deposit_thendisplaynewbalance() throws Exception {
        // given
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";
        
        double depositAmount = 1000.0;
        Iban iban = new Iban("AT12 3456 7890 1234");

        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        AccountDTO newAccountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance + depositAmount)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm depositForm = accountPageBefore.getFormByName("deposit");
        final HtmlSubmitInput submitButton = depositForm.getInputByName("submit");
        final HtmlNumberInput amountInput = depositForm.getInputByName("amount");

        // type in the amount
        amountInput.setValueAttribute("" + depositAmount);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(newAccountInfo);
        // submit form to post the deposit
        final HtmlPage accountPageAfter = submitButton.click();

        // then
        final HtmlHeading3 accountBalanceHeading = (HtmlHeading3) accountPageAfter.getByXPath("//h3").get(0);
        Mockito.verify(accountService, times(1)).deposit(depositAmount, iban.toString());
        assertEquals("Balance: " + (balance + depositAmount), accountBalanceHeading.getTextContent());
    }

    @Test
    public void given_account_when_deposit_and_noaccount_thenshowerror() throws Exception {
        // given
        String expectedErrorMessage = "Error: Account not found!";
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";
        
        double depositAmount = 1000.0;
        Iban iban = new Iban("AT12 3456 7890 1234");

        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm depositForm = accountPageBefore.getFormByName("deposit");
        final HtmlSubmitInput submitButton = depositForm.getInputByName("submit");
        final HtmlNumberInput amountInput = depositForm.getInputByName("amount");

        // type in the amount
        amountInput.setValueAttribute("" + depositAmount);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.doThrow(new AccountNotFoundException("")).when(accountService).deposit(depositAmount, iban.toString());
        // submit form to post the deposit
        final HtmlPage errorPage = submitButton.click();

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) errorPage.getByXPath("//h1").get(0);
        assertEquals("Banking", errorPage.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    @Test
    public void given_account_when_withdraw_andnoaccount_thendisplaynewbalance() throws Exception {
        // given
        String expectedErrorMessage = "Error: Account not found!";
        
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";

        double withdrawAmount = 1000.0;
        Iban iban = new Iban("AT12 3456 7890 1234");

        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm withdrawForm = accountPageBefore.getFormByName("withdraw");
        final HtmlSubmitInput submitButton = withdrawForm.getInputByName("submit");
        final HtmlNumberInput amountInput = withdrawForm.getInputByName("amount");

        // type in the amount
        amountInput.setValueAttribute("" + withdrawAmount);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.doThrow(new AccountNotFoundException("")).when(accountService).withdraw(withdrawAmount, iban.toString());
        // submit form to post the deposit
        final HtmlPage errorPage = submitButton.click();

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) errorPage.getByXPath("//h1").get(0);
        assertEquals("Banking", errorPage.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    @Test
    public void given_account_when_withdraw_thendisplaynewbalance() throws Exception {
        // given
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";

        double withdrawAmount = 1000.0;
        Iban iban = new Iban("AT12 3456 7890 1234");

        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        AccountDTO newAccountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance - withdrawAmount)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm withdrawForm = accountPageBefore.getFormByName("withdraw");
        final HtmlSubmitInput submitButton = withdrawForm.getInputByName("submit");
        final HtmlNumberInput amountInput = withdrawForm.getInputByName("amount");

        // type in the amount
        amountInput.setValueAttribute("" + withdrawAmount);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(newAccountInfo);
        // submit form to post the deposit
        final HtmlPage accountPageAfter = submitButton.click();

        // then
        final HtmlHeading3 accountBalanceHeading = (HtmlHeading3) accountPageAfter.getByXPath("//h3").get(0);
        Mockito.verify(accountService, times(1)).withdraw(withdrawAmount, iban.toString());
        assertEquals("Balance: " + (balance - withdrawAmount), accountBalanceHeading.getTextContent());
    }

    @Test
    public void given_account_when_transferTransactional_thendisplaynewbalance() throws Exception {
        // given
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";
        Iban iban = new Iban("AT12 3456 7890 1234");

        String reference = "Rent";
        double transferAmount = 1000.0;
        Iban receivingIban = new Iban("AT98 7654 3210 9876");
        
        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        AccountDTO newAccountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance - transferAmount)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm transferForm = accountPageBefore.getFormByName("transfer");
        final HtmlSubmitInput submitButton = transferForm.getInputByName("submit");
        final HtmlTextInput receivingIbanInput = transferForm.getInputByName("receivingIban");
        final HtmlNumberInput amountInput = transferForm.getInputByName("amount");
        final HtmlTextInput referenceInput = transferForm.getInputByName("reference");

        // type in the amount
        receivingIbanInput.setValueAttribute(receivingIban.toString());
        amountInput.setValueAttribute("" + transferAmount);
        referenceInput.setValueAttribute(reference);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(newAccountInfo);
        // submit form to post the deposit
        final HtmlPage accountPageAfter = submitButton.click();

        // then
        final HtmlHeading3 accountBalanceHeading = (HtmlHeading3) accountPageAfter.getByXPath("//h3").get(0);
        Mockito.verify(accountService, times(1)).transferTransactional(transferAmount, reference, iban.toString(), receivingIban.toString());
        assertEquals("Balance: " + (balance - transferAmount), accountBalanceHeading.getTextContent());
    }

    @Test
    public void given_account_when_transferTransactional_and_noaccount_thenshowerror() throws Exception {
        // given
        String expectedErrorMessage = "Error: Account not found!";

        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";
        Iban iban = new Iban("AT12 3456 7890 1234");

        String reference = "Rent";
        double transferAmount = 1000.0;
        Iban receivingIban = new Iban("AT98 7654 3210 9876");
        
        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm transferForm = accountPageBefore.getFormByName("transfer");
        final HtmlSubmitInput submitButton = transferForm.getInputByName("submit");
        final HtmlTextInput receivingIbanInput = transferForm.getInputByName("receivingIban");
        final HtmlNumberInput amountInput = transferForm.getInputByName("amount");
        final HtmlTextInput referenceInput = transferForm.getInputByName("reference");

        // type in the amount
        receivingIbanInput.setValueAttribute(receivingIban.toString());
        amountInput.setValueAttribute("" + transferAmount);
        referenceInput.setValueAttribute(reference);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.doThrow(new AccountNotFoundException("")).when(accountService).transferTransactional(transferAmount, reference, iban.toString(), receivingIban.toString());
        // submit form to post the deposit
        final HtmlPage errorPage = submitButton.click();

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) errorPage.getByXPath("//h1").get(0);
        assertEquals("Banking", errorPage.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    @Test
    public void given_account_when_transferTransactional_and_nocustomer_thenshowerror() throws Exception {
        // given
        String expectedErrorMessage = "Error: Customer not found!";
        
        double balance = 1234.0;
        String customerId = "1";
        String customerName = "Jonathan";
        Iban iban = new Iban("AT12 3456 7890 1234");

        String reference = "Rent";
        double transferAmount = 1000.0;
        Iban receivingIban = new Iban("AT98 7654 3210 9876");
        
        AccountDTO accountInfo = AccountDTO.builder()
            .withDetails(AccountDetailsDTO.builder()
                .withIban(iban)
                .withType("GIRO")
                .withBalance(balance)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);
    
        // when
        final HtmlPage accountPageBefore = this.fetchAccountPage(iban.toString(), customerId, customerName);
        final HtmlForm transferForm = accountPageBefore.getFormByName("transfer");
        final HtmlSubmitInput submitButton = transferForm.getInputByName("submit");
        final HtmlTextInput receivingIbanInput = transferForm.getInputByName("receivingIban");
        final HtmlNumberInput amountInput = transferForm.getInputByName("amount");
        final HtmlTextInput referenceInput = transferForm.getInputByName("reference");

        // type in the amount
        receivingIbanInput.setValueAttribute(receivingIban.toString());
        amountInput.setValueAttribute("" + transferAmount);
        referenceInput.setValueAttribute(reference);
        // return different account info with new balance after deposit upon account page redirect
        Mockito.doThrow(new CustomerNotFoundException("")).when(accountService).transferTransactional(transferAmount, reference, iban.toString(), receivingIban.toString());
        // submit form to post the deposit
        final HtmlPage errorPage = submitButton.click();

        // then
        final HtmlHeading1 errorMessage = (HtmlHeading1) errorPage.getByXPath("//h1").get(0);
        assertEquals("Banking", errorPage.getTitleText());
        assertEquals(expectedErrorMessage, errorMessage.getTextContent());
    }

    private HtmlPage fetchAccountPage(String iban, String customerId, String customerName) throws Exception {
        return this.webClient.getPage("http://localhost/account?iban=" + iban + "&id=" + customerId + "&name=" + customerName);
    }
}
