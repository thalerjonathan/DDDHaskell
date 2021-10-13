package at.fhv.se.banking.application.api;

import java.util.List;

import at.fhv.se.banking.application.dto.CustomerDTO;

public interface CustomerService {

    List<CustomerDTO> listAll();
}
