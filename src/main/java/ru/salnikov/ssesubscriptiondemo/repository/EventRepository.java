package ru.salnikov.ssesubscriptiondemo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.salnikov.ssesubscriptiondemo.domain.document.Event;

import java.util.UUID;

/**
 * Репозиторий событий.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
public interface EventRepository extends ReactiveMongoRepository<Event, UUID> {
}
