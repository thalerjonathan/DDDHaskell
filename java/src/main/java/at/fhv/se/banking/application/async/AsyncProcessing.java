package at.fhv.se.banking.application.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.EventProcessingService;

@Profile("!test")
@Component
@EnableScheduling
@Async
public class AsyncProcessing {

    // every 10 seconds
    private static final double EVENT_PROCESSING_FREQUENCY = 0.1;

    @Autowired
    private EventProcessingService eventProc;

    @Scheduled(fixedRate = (int) (1000.0 / EVENT_PROCESSING_FREQUENCY))
    public void runEventProcessing() {
        eventProc.processNextEvent();
    }
}
