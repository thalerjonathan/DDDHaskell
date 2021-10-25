package at.fhv.se.banking.infrastructure.db;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import at.fhv.se.banking.domain.events.DomainEvent;
import at.fhv.se.banking.domain.repositories.EventRepository;
import at.fhv.se.banking.infrastructure.db.events.PersistedEvent;
import at.fhv.se.banking.infrastructure.db.events.PersistedEventRepository;
import at.fhv.se.banking.infrastructure.db.utils.Utils;

@Component
public class HibernatePersistedEventRepository implements PersistedEventRepository, EventRepository {

    @PersistenceContext
	private EntityManager em;

    @Override
    public void persistDomainEvent(DomainEvent event) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		LocalDateTime created = LocalDateTime.now();
		String type = event.getClass().getSimpleName();
		
		try {
			String payload = objectMapper.writeValueAsString(event);
			PersistedEvent pe = new PersistedEvent(created, type, payload);
			this.em.persist(pe);
		} catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
		}
    }

    @Override
    public Optional<PersistedEvent> nextEvent() {
        TypedQuery<PersistedEvent> query = this.em.createQuery("FROM PersistedEvent WHERE processed = :processed AND failed = false ORDER BY created", PersistedEvent.class);
		query.setParameter("processed", false);
		query.setMaxResults(1);

		return Utils.getOptionalResult(query);
    }

    @Override
    public void remove(PersistedEvent pe) {
        this.em.remove(pe);
    }
}
