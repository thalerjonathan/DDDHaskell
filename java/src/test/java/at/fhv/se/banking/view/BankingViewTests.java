package at.fhv.se.banking.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

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
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.AccountInfoDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerInfoDTO;
import at.fhv.se.banking.application.dto.TXLineDTO;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.Iban;
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
    }

    @AfterEach
    void teardown() {
        this.webClient.close();
    }

    @Test
    public void given_customers_when_welcomepage_displaycustomers() throws Exception {
        // given
        List<CustomerDTO> customers = Arrays.asList(
            CustomerDTO.create().withName("Alice").build(),
            CustomerDTO.create().withName("Bob").build()
        );
        Mockito.when(customerService.listAll()).thenReturn(customers);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");

        // then
        assertEquals("Banking", page.getTitleText(), "Mismatching page title text");

        final List<HtmlAnchor> customerButtons = page.getByXPath("//ul//a");
        TestingUtils.assertEqualsCollections(customerButtons, customers, (cb, dto) -> {
            String href = cb.getHrefAttribute();

            assertEquals("/customer?name=" + dto.name(), href);
            assertEquals(dto.name(), cb.asNormalizedText());
        });
    }

    @Test
    public void given_customer_when_view_displaydetailsandaccounts() throws Exception {
        // given
        String customerName = "Jonathan";
        CustomerInfoDTO customerInfo = CustomerInfoDTO.create()
            .withCustomer(CustomerDTO.create().withName(customerName).build())
            .addAccountInfo(AccountInfoDTO.create()
                .withIban(new Iban("AT12 3456 7890 1234"))
                .withType(AccountType.GIRO)
                .withBalance(1234.5).build())
            .addAccountInfo(AccountInfoDTO.create()
                .withIban(new Iban("AT98 7654 3210 9876"))
                .withType(AccountType.SAVINGS)
                .withBalance(1234.5).build())
            .build();

        Mockito.when(customerService.informationFor(customerName)).thenReturn(customerInfo);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/customer?name=" + customerName);

        // then
        final HtmlHeading1 heading = (HtmlHeading1) page.getByXPath("//h1").get(0);
        final List<HtmlAnchor> accountButtons = page.getByXPath("//ul//a");

        assertEquals("Banking", page.getTitleText(), "Mismatching page title text");
        assertEquals(customerName, heading.getTextContent(), "Mismatching page title text");

        TestingUtils.assertEqualsCollections(accountButtons, customerInfo.accountInfos(), (ab, dto) -> {
            String href = ab.getHrefAttribute();
            String expectedHref = "/account?iban=" +
                dto.iban().replace(" ", "%20") + "&customer=" + 
                customerName;

            String accountLinkText = ab.asNormalizedText();
            String expectedLinkText = dto.iban() + " " + dto.balance();
            
            assertEquals(expectedHref, href);
            assertEquals(expectedLinkText, accountLinkText);
        });
    }

    @Test
    public void given_account_when_view_displayinfoandalltransactions() throws Exception {
        // given
        String customerName = "Jonathan";
        Iban iban = new Iban("AT 12 3456 7890 1234");
        AccountDTO accountInfo = AccountDTO.create()
            .withInfo(AccountInfoDTO.create()
                .withIban(iban)
                .withType(AccountType.GIRO)
                .withBalance(1234.5)
                .build())
            .addTXLine(TXLineDTO.create()
                .withIban(new Iban("AT98 7654 3210 9876"))
                .withName("Hans Huber")
                .ofAmount(-100.0)
                .withReference("Lunch")
                .build())
            .addTXLine(TXLineDTO.create()
                .withIban(new Iban("AT11 2222 3333 4444"))
                .withName("Max Mustermann")
                .ofAmount(200.0)
                .withReference("Rent")
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/account?iban=" + iban + "&customer=" + customerName);

        // then
        final HtmlHeading1 customerHeading = (HtmlHeading1) page.getByXPath("//h1").get(0);
        final HtmlHeading2 accountIbanHeading = (HtmlHeading2) page.getByXPath("//h2").get(0);

        final List<HtmlListItem> txItems = page.getByXPath("//ul//li");
    
        assertEquals("Banking", page.getTitleText(), "Mismatching page title text");

        assertEquals(customerName, customerHeading.getTextContent(), "Mismatching heading title text");
        assertEquals(iban.toString(), accountIbanHeading.getTextContent(), "Mismatching heading title text");

        TestingUtils.assertEqualsCollections(txItems, accountInfo.txLines(), (li, txLine) -> {
            final List<HtmlParagraph> ps = li.getByXPath("p");

            assertEquals("Amount: " + txLine.amount(), ps.get(0).getTextContent());
            assertEquals("Name: " + txLine.name(), ps.get(1).getTextContent());
            assertEquals("Iban: " + txLine.iban(), ps.get(2).getTextContent());
            assertEquals("Reference: " + txLine.reference(), ps.get(3).getTextContent());
        });
    }

    @Test
    public void given_account_when_deposit_thendisplay() throws Exception {
        // given
        String customerName = "Jonathan";
        Iban iban = new Iban("AT 12 3456 7890 1234");
        AccountDTO accountInfo = AccountDTO.create()
            .withInfo(AccountInfoDTO.create()
                .withIban(iban)
                .withType(AccountType.GIRO)
                .withBalance(1234.5)
                .build())
            .build();

        Mockito.when(accountService.accountByIban(iban.toString())).thenReturn(accountInfo);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/account?iban=" + iban + "&customer=" + customerName);

        // then
        final HtmlForm depositForm = page.getFormByName("deposit");
        depositForm.getInputByName("amount");
    }

    // TODO write test for withdraw form
    // TODO write test for transfer form
}
