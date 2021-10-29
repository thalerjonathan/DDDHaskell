package at.fhv.se.banking.bdd.runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/", 
    glue = {"at.fhv.se.banking.bdd"},
    plugin = {"pretty", "html:build/cucumber/banking.html"})
public class CucumberTestRunner {
}
