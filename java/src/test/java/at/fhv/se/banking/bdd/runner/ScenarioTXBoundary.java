package at.fhv.se.banking.bdd.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

// Intended as a Scenario-level TX boundary rather than @Transactional (which runs on the method level)
public class ScenarioTXBoundary {   
    @Autowired
    PlatformTransactionManager txManager;

    private TransactionStatus tx;
    
    // NOTE: intented to be called @Before to start a TX
    protected void beginTX(){       
        this.tx = this.txManager.getTransaction(new DefaultTransactionDefinition());
    }

    // NOTE: intended to be called in an @After to roll back all changes made to the DB in the Scenario
    protected void rollbackTX(){
        this.txManager.rollback(this.tx);
    }
}
