package at.fhv.se.banking.infrastructure.db.events;

import java.util.Optional;

public interface PersistedEventRepository {
    Optional<PersistedEvent> nextEvent();
    void remove(PersistedEvent pe);
}
