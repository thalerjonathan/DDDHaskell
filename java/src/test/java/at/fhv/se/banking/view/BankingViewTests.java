package at.fhv.se.banking.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.utils.TestingUtils;


@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
public class BankingViewTests {
    
    private WebClient webClient;

    @MockBean
    private CustomerService customerService;

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

        assertEquals("Banking Customers", page.getTitleText(), "Mismatching page title text");

        final List<HtmlAnchor> customerButtons = page.getByXPath("//ul//a");
        TestingUtils.assertEqualsCollections(customerButtons, customers, (cb, dto) -> {
            assertEquals(dto.name(), cb.asNormalizedText());
        });
    }
}
