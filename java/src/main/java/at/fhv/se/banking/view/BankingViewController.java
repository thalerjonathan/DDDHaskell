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
import at.fhv.se.banking.application.api.exceptions.AccountNotFoundException;
import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.api.exceptions.InvalidOperationException;
import at.fhv.se.banking.application.dto.AccountDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.view.forms.AccountForm;

@Controller
public class BankingViewController {
    
    private static final String ALL_CUSTOMERS_URL = "/";
    private static final String CUSTOMER_URL = "/customer";
    private static final String ACCOUNT_URL = "/account";
    private static final String ERROR_URL = "/displayerror";

    private static final String POST_DEPOSIT_URL = "/account/deposit";
    private static final String POST_WITHDRAW_URL = "/account/withdraw";
    private static final String POST_TRANSFER_URL = "/account/transfer";

    private static final String ALL_CUSTOMERS_VIEW = "allCustomers";
    private static final String CUSTOMER_VIEW = "customer";
    private static final String ACCOUNT_VIEW = "account";
    private static final String ERROR_VIEW = "errorView";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @GetMapping(ALL_CUSTOMERS_URL)
    public String allCustomers(Model model) {
        final List<CustomerDetailsDTO> customers = this.customerService.listAll();
        model.addAttribute("customers", customers);

        return ALL_CUSTOMERS_VIEW;
    }

    @GetMapping(CUSTOMER_URL)
    public ModelAndView customer(
            @RequestParam("id") String customerId, 
            Model model) {
        try {
            final CustomerDTO customerDetails = this.customerService.detailsFor(customerId);
            model.addAttribute("customer", customerDetails);
            return new ModelAndView(CUSTOMER_VIEW);
        } catch (CustomerNotFoundException e) {
            return redirectError("Customer not found!");
        }
    }

    @GetMapping(ACCOUNT_URL)
    public ModelAndView accountInfo(
            @RequestParam("iban") String iban, 
            @RequestParam("id") String customerId, 
            @RequestParam("name") String customerName, 
            Model model) {

        try {
            final AccountDTO account = this.accountService.accountByIban(iban);
            final AccountForm form = new AccountForm(customerId, customerName, iban);

            model.addAttribute("account", account);
            model.addAttribute("form", form);
            return new ModelAndView(ACCOUNT_VIEW);
        } catch (AccountNotFoundException e) {
            return redirectError("Account not found!");
        }
    }

    @PostMapping(POST_DEPOSIT_URL)
    public ModelAndView depositAccount(
            @ModelAttribute AccountForm form, 
            Model model) {

        try {
            this.accountService.deposit(form.getIban(), form.getAmount());
            return redirectToAccount(form);
        } catch (AccountNotFoundException e) {
            return redirectError("Account not found!");
        } catch (InvalidOperationException e) {
            return redirectError(e.getMessage());
        }
    }

    @PostMapping(POST_WITHDRAW_URL)
    public ModelAndView withdrawAccount(
            @ModelAttribute AccountForm form, 
            Model model) {

        try {
            this.accountService.withdraw(form.getIban(), form.getAmount());
            return redirectToAccount(form);
        } catch (AccountNotFoundException e) {
            return redirectError("Account not found!");
        } catch (InvalidOperationException e) {
            return redirectError(e.getMessage());
        }
    }

    @PostMapping(POST_TRANSFER_URL)
    public ModelAndView transferAccount(@ModelAttribute AccountForm form,
            @RequestParam("receivingIban") String receivingIban, 
            @RequestParam("reference") String reference, 
            Model model) {

        try {
            this.accountService.transfer(form.getIban(), receivingIban, form.getAmount(), reference);
            return redirectToAccount(form);
        } catch (AccountNotFoundException e) {
            return redirectError("Account not found!");
        } catch (CustomerNotFoundException e) {
            return redirectError("Customer not found!");
        } catch (InvalidOperationException e) {
            return redirectError(e.getMessage());
        }
    }

    @GetMapping(ERROR_URL)
    public String displayError(@RequestParam("msg") String msg, Model model) {
        model.addAttribute("msg", msg);
        return ERROR_VIEW;
    }

    private static ModelAndView redirectError(String msg) {
        return new ModelAndView("redirect:" + ERROR_URL + "?msg=" + msg);
    } 

    private static ModelAndView redirectToAccount(final AccountForm form) {
        return new ModelAndView("redirect:" + 
            ACCOUNT_URL + 
            "?iban=" + 
            form.getIban() + 
            "&id=" + 
            form.getCustomerId() + 
            "&name=" + 
            form.getCustomerName());
    }
}
