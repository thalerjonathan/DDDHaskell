package at.fhv.se.banking.infrastructure.db.events;

import java.util.Date;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.fhv.se.banking.domain.events.DomainEvent;

public class PersistedEvent {
    private Long id;
    private Date created;
    private String type;
    private boolean processed;
    private boolean failed;
    private String failMessage;
    private String payload;

    public PersistedEvent(Date created, String type, String payload) {
        this.created = created;
        this.type = type;
        this.payload = payload;
    }

    public Long id() {
        return this.id;
    }

    public Date created() {
        return created;
    }

    public String type() {
        return type;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public String failMessage() {
        return this.failMessage;
    }

    public void markProcessed() {
        this.processed = true;
    }

    public void markFailed(String message) {
        this.failed = true;
        this.failMessage = message;
    }

    public <T extends DomainEvent> Optional<T> domainEventOf(Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        try {
            T evt = objectMapper.readValue(this.payload, clazz);
            return Optional.of(evt);
        } catch (JsonProcessingException e) {
//            System.out.println(e);
        }
        
        return Optional.empty();
    }

    // NOTE: needed by Hibernate
    @SuppressWarnings("unused")
    private PersistedEvent() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersistedEvent other = (PersistedEvent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    
}

