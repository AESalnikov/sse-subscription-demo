package ru.salnikov.ssesubscriptiondemo.consumer;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;
import ru.salnikov.ssesubscriptiondemo.domain.mapper.EventMapper;
import ru.salnikov.ssesubscriptiondemo.repository.EventRepository;

import java.util.UUID;

/**
 * Потребитель событий.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Component
public class EventConsumer {

    /**
     * Потребляет события и сохраняет их в монго.
     * @param eventMapper маппер событий
     * @param eventRepository репозиторий событий
     * @param eventStream поток событий мз топика
     */
    @Autowired
    public void consume(final EventMapper eventMapper, final EventRepository eventRepository, final KStream<UUID, EventDto> eventStream) {
        eventStream.foreach(
                (key, value) ->
                        eventRepository.save(
                                eventMapper.fromDto(value)
                        ).subscribe()
        );
    }

}
