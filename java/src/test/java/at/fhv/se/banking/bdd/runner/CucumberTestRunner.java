package at.fhv.se.banking.bdd.runner;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/", 
    glue = {"at.fhv.se.banking.bdd"},
    plugin = {"pretty", "html:build/cucumber/banking.html"})
@CucumberContextConfiguration
@SpringBootTest
public class CucumberTestRunner {
}
