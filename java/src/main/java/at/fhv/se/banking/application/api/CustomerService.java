package at.fhv.se.banking.application.api;

import java.util.List;

import at.fhv.se.banking.application.dto.CustomerDTO;
import at.fhv.se.banking.application.dto.CustomerInfoDTO;

public interface CustomerService {

    List<CustomerDTO> listAll();

    CustomerInfoDTO informationFor(String customerName);
}
