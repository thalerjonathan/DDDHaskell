package at.fhv.se.banking.domain.repositories;

import at.fhv.se.banking.domain.events.DomainEvent;

public interface EventRepository {
    void persistDomainEvent(DomainEvent event) throws Exception;
}
