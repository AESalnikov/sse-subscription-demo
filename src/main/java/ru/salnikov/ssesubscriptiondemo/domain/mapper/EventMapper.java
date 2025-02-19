package ru.salnikov.ssesubscriptiondemo.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.salnikov.ssesubscriptiondemo.domain.document.Event;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер событий.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Mapper(componentModel = SPRING)
public interface EventMapper {

    /**
     * Маппинг события из ДТО события.
     * @param eventDto ДТО события
     * @return событие
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "eventId", source = "id")
    Event fromDto(EventDto eventDto);

    /**
     * Маппинг события в ДТО.
     * @param event событие
     * @return ДТО события
     */
    @Mapping(target = "id", source = "eventId")
    EventDto toDto(Event event);

}
