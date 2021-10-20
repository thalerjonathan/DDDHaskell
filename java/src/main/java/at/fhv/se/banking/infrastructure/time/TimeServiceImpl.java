package at.fhv.se.banking.infrastructure.time;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.application.api.TimeService;

@Component
public class TimeServiceImpl implements TimeService {

    @Override
    public LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
