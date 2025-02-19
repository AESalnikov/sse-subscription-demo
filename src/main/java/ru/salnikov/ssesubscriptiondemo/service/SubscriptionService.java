package ru.salnikov.ssesubscriptiondemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.salnikov.ssesubscriptiondemo.domain.document.Event;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;
import ru.salnikov.ssesubscriptiondemo.domain.mapper.EventMapper;

import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Сервис для управления подписками.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final String EVENT_ID = "eventId";
    private static final String EVENT_COLLECTION_NAME = "event";

    private final EventMapper eventMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    /**
     * Подписка на события с определенным идентификатором.
     * @param subscriptionId идентификатор событий
     * @return поток событий
     */
    public Flux<ServerSentEvent<EventDto>> subscribeById(UUID subscriptionId) {
        return Flux.concat(getEventHistoryStream(subscriptionId), getEventChangeStream(subscriptionId))
                .map(eventMapper::toDto)
                .map(event -> ServerSentEvent.<EventDto>builder().data(event).build());
    }

    private Flux<Event> getEventChangeStream(UUID subscriptionId) {
        return reactiveMongoTemplate.changeStream(
                EVENT_COLLECTION_NAME,
                getChangerStreamOptions(subscriptionId),
                Event.class
        ).mapNotNull(ChangeStreamEvent::getBody);
    }

    private MatchOperation getChangeStreamMatchOperation(UUID subscriptionId) {
        return match(isEventIdIsSubscriptionId(subscriptionId));
    }

    private ChangeStreamOptions getChangerStreamOptions(UUID subscriptionId) {
        return ChangeStreamOptions.builder()
                .filter(
                        newAggregation(
                                getChangeStreamMatchOperation(subscriptionId)
                        )
                )
                .build();
    }

    private Flux<Event> getEventHistoryStream(UUID subscriptionId) {
        return reactiveMongoTemplate.find(
                findAllByEventId(subscriptionId),
                Event.class
        );
    }

    private Query findAllByEventId(UUID subscriptionId) {
        return new Query(
                isEventIdIsSubscriptionId(subscriptionId)
        );
    }

    private Criteria isEventIdIsSubscriptionId(UUID subscriptionId) {
        return where(EVENT_ID).is(subscriptionId);
    }

}
