package at.fhv.se.banking.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fhv.se.banking.application.api.CustomerService;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerInfoDTO;

@Controller
public class BankingViewController {
    
    private static final String ALL_CUSTOMERS_URL = "/";
    private static final String CUSTOMER_INFO_URL = "/customer";

    private static final String ALL_CUSTOMERS_VIEW = "allCustomers";
    private static final String CUSTOMER_INFO = "customerInfo";

    @Autowired
    private CustomerService customerService;

    @GetMapping(ALL_CUSTOMERS_URL)
    public String allCustomers(Model model) {
        final List<CustomerDTO> customers = this.customerService.listAll();
        model.addAttribute("customers", customers);

        return ALL_CUSTOMERS_VIEW;
    }

    @GetMapping(CUSTOMER_INFO_URL)
    public String customerInfo(@RequestParam("name") String customerName, Model model) {
        final CustomerInfoDTO customerInfo = this.customerService.informationFor(customerName);
        model.addAttribute("info", customerInfo);

        return CUSTOMER_INFO;
    }
}
