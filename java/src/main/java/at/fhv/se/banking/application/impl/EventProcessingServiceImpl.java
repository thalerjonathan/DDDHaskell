package at.fhv.se.banking.application.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.AccountService;
import at.fhv.se.banking.application.api.EventProcessingService;
import at.fhv.se.banking.domain.events.TransferFailed;
import at.fhv.se.banking.domain.events.TransferSent;
import at.fhv.se.banking.domain.repositories.EventRepository;
import at.fhv.se.banking.infrastructure.db.entities.PersistedEvent;

@Component
public class EventProcessingServiceImpl implements EventProcessingService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private AccountService accountService;

    private final static String DESERIALISATION_FAILURE = "Deserialisation failed";

    @Transactional
    @Override
    public void processNextEvent() {
        Optional<PersistedEvent> eOpt = eventRepo.nextEvent();
        eOpt.ifPresent(pe -> {
            if (pe.type().equals(TransferSent.class.getSimpleName())) {
                Optional<TransferSent> evt = pe.domainEventOf(TransferSent.class);
                if (evt.isPresent()) {
                    try {
                        TransferSent e = evt.get();
                        accountService.transferReceive(e);
                        
                        eventRepo.remove(pe);
                    } catch (Exception e) {
                        pe.markFailed(e.getMessage());
                    }
                } else {
                    pe.markFailed(DESERIALISATION_FAILURE);
                }

            } else if (pe.type().equals(TransferSent.class.getSimpleName())) {
                Optional<TransferFailed> evt = pe.domainEventOf(TransferFailed.class);
                if (evt.isPresent()) {
                    try {
                        TransferFailed e = evt.get();
                        accountService.transferFailed(e);
                        
                        eventRepo.remove(pe);
                    } catch (Exception e) {
                        pe.markFailed(e.getMessage());
                    }
                } else {
                    pe.markFailed(DESERIALISATION_FAILURE);
                }
            // mark all others as processed, so they do not show up again
            } else {
                pe.markProcessed();
            }
        });
    }
}
