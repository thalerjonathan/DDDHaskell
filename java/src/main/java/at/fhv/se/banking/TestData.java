package at.fhv.se.banking;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.model.Account;
import at.fhv.se.banking.domain.model.AccountType;
import at.fhv.se.banking.domain.model.Customer;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.Iban;
import at.fhv.se.banking.domain.model.TXLine;
import at.fhv.se.banking.domain.repositories.AccountRepository;
import at.fhv.se.banking.domain.repositories.CustomerRepository;

@Component
public class TestData implements ApplicationRunner {
    
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CustomerId cId1 = customerRepo.nextCustomerId();
        CustomerId cId2 = customerRepo.nextCustomerId();
        Customer customer1 = new Customer(cId1, "Jonathan Thaler");
        Customer customer2 = new Customer(cId2, "Thomas Scharz");

        Account account1Customer1 = new Account(cId1, new Iban("AT11 1111 1111 1111"), AccountType.GIRO, 1234);
        account1Customer1.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now()));
        account1Customer1.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now()));
        Account account2Customer1 = new Account(cId1, new Iban("AT22 2222 2222 2222"), AccountType.SAVINGS, 2345);
        account2Customer1.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 345, "Max Mustermann", "Rent", LocalDateTime.now()));
        account2Customer1.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 345, "Max Mustermann", "Rent", LocalDateTime.now()));

        Account account1Customer2 = new Account(cId2, new Iban("AT33 3333 3333 3333"), AccountType.GIRO, 3456);
        account1Customer2.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now()));
        account1Customer2.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 123, "Max Mustermann", "Rent", LocalDateTime.now()));
        Account account2Customer2 = new Account(cId2, new Iban("AT44 4444 4444 4444"), AccountType.SAVINGS, 4567);
        account2Customer2.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 345, "Max Mustermann", "Rent", LocalDateTime.now()));
        account2Customer2.addTXLine(new TXLine(new Iban("AT98 7654 3210 9876"), 345, "Max Mustermann", "Rent", LocalDateTime.now()));

        accountRepo.add(account1Customer1);
        accountRepo.add(account2Customer1);
        accountRepo.add(account1Customer2);
        accountRepo.add(account2Customer2);

        customerRepo.add(customer1);
        customerRepo.add(customer2);
    }

}
