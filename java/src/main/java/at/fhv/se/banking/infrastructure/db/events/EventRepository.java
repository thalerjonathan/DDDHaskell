package at.fhv.se.banking.infrastructure.db.events;

import java.util.Optional;

import at.fhv.se.banking.domain.events.DomainEvent;

public interface EventRepository {
    // TODO: split into multiple repositories
    // EventRepository with persistDomainEVent -> into Domain Model
    // PersistedEventRepository with nextEvent and remove -> into Infrastructure
    Optional<PersistedEvent> persistDomainEvent(DomainEvent event);
    Optional<PersistedEvent> nextEvent();
    void remove(PersistedEvent pe);
}
