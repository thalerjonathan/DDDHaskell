package at.fhv.se.banking.application.api;

import java.util.List;

import at.fhv.se.banking.application.api.exceptions.CustomerNotFoundException;
import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerDetailsDTO;

public interface CustomerService {

    List<CustomerDTO> listAll();
    CustomerDetailsDTO detailsFor(String customerId) throws CustomerNotFoundException;
}
