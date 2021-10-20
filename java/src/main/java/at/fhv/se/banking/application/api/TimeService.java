package at.fhv.se.banking.application.api;

import java.time.LocalDateTime;

public interface TimeService {
    
    LocalDateTime utcNow();
}
