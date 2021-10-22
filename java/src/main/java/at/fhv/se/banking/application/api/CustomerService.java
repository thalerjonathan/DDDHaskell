package at.fhv.se.banking.application.api;

import java.util.List;

import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;
import at.fhv.se.banking.application.dto.CustomerDTO;

public interface CustomerService {

    List<CustomerDetailsDTO> listAll();
    CustomerDTO detailsFor(String customerId) throws CustomerNotFoundException;
}
