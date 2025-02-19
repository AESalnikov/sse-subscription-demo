package ru.salnikov.ssesubscriptiondemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;
import ru.salnikov.ssesubscriptiondemo.service.SubscriptionService;

import java.util.UUID;

/**
 * Контроллер событий.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final SubscriptionService subscriptionService;

    /**
     * Подписка на поступающие события с определенным идентификатором.
     * @param subscriptionId идентификатор подписки
     * @return поток событий
     */
    @GetMapping("/subscribe")
    public Flux<ServerSentEvent<EventDto>> subscribe(@RequestParam UUID subscriptionId) {
        return subscriptionService.subscribeById(subscriptionId);
    }

}
