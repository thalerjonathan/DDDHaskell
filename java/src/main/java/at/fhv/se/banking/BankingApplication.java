package at.fhv.se.banking;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingApplication {
	//private static Logger LOG = LoggerFactory.getLogger(BankingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}
}
