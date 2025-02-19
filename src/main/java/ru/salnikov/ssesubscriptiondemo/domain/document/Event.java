package ru.salnikov.ssesubscriptiondemo.domain.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Модель события.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Getter
@Setter
@Document
public class Event {

    /**
     * Идентификатор документа.
     */
    @Id
    private String id;

    /**
     * Версия документа.
     */
    @Version
    private Long version;

    /**
     * Дата создания записи.
     */
    @CreatedDate
    @Indexed(name = "ttl_index", expireAfter = "60s")
    private Instant createdDate;

    /**
     * Идентификатор события.
     */
    private UUID eventId;

    /**
     * Заголовок события.
     */
    private String title;

    /**
     * Описание события.
     */
    private String description;

}
