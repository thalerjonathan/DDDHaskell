package at.fhv.se.banking.domain.repositories;

import java.util.Optional;

import at.fhv.se.banking.domain.events.DomainEvent;
import at.fhv.se.banking.infrastructure.db.entities.PersistedEvent;

public interface EventRepository {
    void persistDomainEvent(DomainEvent event) throws Exception;
    Optional<PersistedEvent> nextEvent();
    void remove(PersistedEvent pe);
}
