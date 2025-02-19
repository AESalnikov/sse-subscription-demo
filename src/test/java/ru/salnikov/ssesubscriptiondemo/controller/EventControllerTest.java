package ru.salnikov.ssesubscriptiondemo.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import ru.salnikov.ssesubscriptiondemo.domain.document.Event;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;
import ru.salnikov.ssesubscriptiondemo.repository.EventRepository;
import ru.salnikov.ssesubscriptiondemo.test.base.AbstractIntegrationTests;

import java.time.Duration;
import java.util.UUID;

import static java.time.Duration.ofMillis;
import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

/**
 * Интеграционный тест для контроллера {@link EventController}.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Slf4j
class EventControllerTest extends AbstractIntegrationTests {

    private static final long RETRY_MAX_ATTEMPTS = 3;
    private static final Duration RETRY_MIN_BACKOFF = ofMillis(500);
    private static final String TOPIC = "sse-subscription-demo-topic";
    private static final UUID EVENT_ID = fromString("00000000-0000-0000-0000-000000000001");
    private static final String EVENT_TITLE = "Test title";
    private static final String EVENT_DESCRIPTION = "Test description";
    private static final String EVENT_SUBSCRIPTION_PATH = "/event/subscribe";
    private static final String SUBSCRIPTION_ID = "subscriptionId";

    @Autowired
    private KafkaTemplate<UUID, EventDto> kafkaTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @SneakyThrows
    @DisplayName("Получить событие, которое было сохранено в базу данных после подписки")
    void shouldReceiveEventPersistedAfterSubscription1() {
        var expectedEvent = createExpectedEvent();
        var eventDto = new EventDto(EVENT_ID, EVENT_TITLE, EVENT_DESCRIPTION);
        var taskExecutor = new VirtualThreadTaskExecutor();

        var assertEventReceived = taskExecutor.submitCompletable(() -> assertEventReceived(eventDto));
        var assertEventSentAndEventStored = taskExecutor.submitCompletable(
                () -> {
                    assertEventSent(eventDto);
                    assertEventStored(expectedEvent);
                }
        );

        assertEventSentAndEventStored.join();
        assertEventReceived.join();
    }

    @Test
    @SneakyThrows
    @DisplayName("Получить событие, которое было сохранено в базу данных до подписки")
    void shouldReceiveEventPersistedBeforeSubscription() {
        var eventDto = new EventDto(EVENT_ID, EVENT_TITLE, EVENT_DESCRIPTION);
        var expectedEvent = createExpectedEvent();

        assertEventSent(eventDto);
        assertEventStored(expectedEvent);
        assertEventReceived(eventDto);
    }

    private void assertEventSent(EventDto eventDto) {
        StepVerifier.create(Mono.fromFuture(kafkaTemplate.send(TOPIC, EVENT_ID, eventDto))
                        .mapNotNull(SendResult::getProducerRecord)
                        .mapNotNull(ProducerRecord::value))
                .expectNext(eventDto)
                .expectComplete()
                .verify();
    }

    private void assertEventStored(Event expectedEvent) {
        StepVerifier.create(eventRepository.findAll()
                        .filter(event -> event.getEventId().equals(EVENT_ID))
                        .single()
                        .retryWhen(Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_MIN_BACKOFF)))
                .expectSubscription()
                .assertNext(event -> assertThat(event)
                        .isNotNull()
                        .usingRecursiveComparison()
                        .ignoringFields("id", "version", "createdDate")
                        .isEqualTo(expectedEvent))
                .expectComplete()
                .verify();
    }

    private void assertEventReceived(EventDto expectedEventDto) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder.path(EVENT_SUBSCRIPTION_PATH)
                        .queryParam(SUBSCRIPTION_ID, EVENT_ID.toString())
                        .build())
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(EventDto.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(expectedEventDto)
                .thenCancel()
                .verify();
    }

    private Event createExpectedEvent() {
        var event = new Event();
        event.setEventId(EVENT_ID);
        event.setTitle(EVENT_TITLE);
        event.setDescription(EVENT_DESCRIPTION);
        return event;
    }

}
