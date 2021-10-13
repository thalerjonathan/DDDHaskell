package at.fhv.se.banking.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.dto.CustomerDTO;

@Controller
public class BankingViewController {
    
    private static final String ALL_CUSTOMERS_URL = "/";

    private static final String ALL_CUSTOMERS_VIEW = "allCustomers";

    @Autowired
    private CustomerService customerService;

    @GetMapping(ALL_CUSTOMERS_URL)
    public String allCustomers(Model model) {
        final List<CustomerDTO> customers = this.customerService.listAll();
        model.addAttribute("customers", customers);

        return ALL_CUSTOMERS_VIEW;
    }
}
