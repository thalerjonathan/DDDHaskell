package at.fhv.se.banking.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerInfoDTO;
import at.fhv.se.banking.view.forms.AccountForm;

@Controller
public class BankingViewController {
    
    private static final String GET_ALL_CUSTOMERS_URL = "/";
    private static final String GET_CUSTOMER_INFO_URL = "/customer";
    private static final String GET_ACCOUNT_URL = "/account";

    private static final String POST_DEPOSIT_URL = "/account/deposit";
    private static final String POST_WITHDRAW_URL = "/account/withdraw";
    private static final String POST_TRANSFER_URL = "/account/transfer";

    private static final String ALL_CUSTOMERS_VIEW = "allCustomers";
    private static final String CUSTOMER_INFO_VIEW = "customerInfo";
    private static final String ACCOUNT_VIEW = "account";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @GetMapping(GET_ALL_CUSTOMERS_URL)
    public String allCustomers(Model model) {
        final List<CustomerDTO> customers = this.customerService.listAll();
        model.addAttribute("customers", customers);

        return ALL_CUSTOMERS_VIEW;
    }

    @GetMapping(GET_CUSTOMER_INFO_URL)
    public String customerInfo(@RequestParam("name") String customerName, Model model) {
        try {
            CustomerInfoDTO customerInfo = this.customerService.informationFor(customerName);
            model.addAttribute("info", customerInfo);
        } catch (CustomerNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return CUSTOMER_INFO_VIEW;
    }

    @GetMapping(GET_ACCOUNT_URL)
    public String accountInfo(@RequestParam("iban") String iban, @RequestParam("customer") String customer, Model model) {
        final AccountDTO accountInfo = this.accountService.accountByIban(iban);
        final AccountForm form = new AccountForm(customer, iban);

        model.addAttribute("account", accountInfo);
        model.addAttribute("form", form);
        
        return ACCOUNT_VIEW;
    }

    @PostMapping(POST_DEPOSIT_URL)
    public ModelAndView depositAccount(@ModelAttribute AccountForm form, Model model) {
        this.accountService.deposit(form.getIban(), form.getAmount());

        return new ModelAndView("redirect:" + 
            GET_ACCOUNT_URL + 
            "?iban=" + 
            form.getIban() + 
            "&customer=" + 
            form.getCustomer());
    }

    @PostMapping(POST_WITHDRAW_URL)
    public ModelAndView withdrawAccount(@ModelAttribute AccountForm form, Model model) {
        this.accountService.withdraw(form.getIban(), form.getAmount());

        return new ModelAndView("redirect:" + 
            GET_ACCOUNT_URL + 
            "?iban=" + 
            form.getIban() + 
            "&customer=" + 
            form.getCustomer());
    }

    @PostMapping(POST_TRANSFER_URL)
    public ModelAndView transferAccount(@ModelAttribute AccountForm form,
            @RequestParam("receivingIban") String receivingIban, 
            @RequestParam("reference") String reference, 
            Model model) {
        this.accountService.transfer(form.getIban(), receivingIban, form.getAmount(), reference);

        return new ModelAndView("redirect:" + 
            GET_ACCOUNT_URL + 
            "?iban=" + 
            form.getIban() + 
            "&customer=" + 
            form.getCustomer());
    } 
}
