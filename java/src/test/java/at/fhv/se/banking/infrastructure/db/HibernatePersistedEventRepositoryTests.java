package at.fhv.se.banking.infrastructure.db;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.banking.domain.events.DomainEvent;
import at.fhv.se.banking.domain.events.TransferSent;
import at.fhv.se.banking.domain.model.CustomerId;
import at.fhv.se.banking.domain.model.account.Iban;
import at.fhv.se.banking.infrastructure.db.entities.PersistedEvent;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class HibernatePersistedEventRepositoryTests {
    
    @Autowired
    private HibernatePersistedEventRepository eventRepo;

    @PersistenceContext
    private EntityManager em;

    @Test
    void given_emptyeventtable_when_nextevent_then_returnempty() {
        // given ... when
        Optional<PersistedEvent> event = eventRepo.nextEvent();

        // then
        assertSame(Optional.empty(), event);
    }

    @Test
    void given_domainevent_when_persisted_then_returnasnext() throws Exception {
        // given
        double amount = 500;
        String reference = "Rent";

        Iban sendingAccount = new Iban("AT12 3456 7890 1234");
        Iban receivingAccount = new Iban("AT98 7654 3210 9876");

        CustomerId sendingCustomer = new CustomerId("1");
        CustomerId receivingCustomer = new CustomerId("2");

        DomainEvent transferSent = new TransferSent(
            amount,
            reference,
            sendingCustomer, 
            receivingCustomer,
            sendingAccount,
            receivingAccount);

        // when
        this.eventRepo.persistDomainEvent(transferSent);
        this.em.flush();

        // then
        PersistedEvent pe = this.eventRepo.nextEvent().get();
        TransferSent dbTransferSent = pe.domainEventOf(TransferSent.class).get();
        assertEquals(transferSent, dbTransferSent);
    }
}
