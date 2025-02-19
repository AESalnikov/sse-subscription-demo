package ru.salnikov.ssesubscriptiondemo.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки кафки.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@ConfigurationProperties(prefix = "spring.kafka")
public record KafkaStreamsProperty(
        String bootstrapServers,
        String applicationId,
        Subscription subscription
) {
    public record Subscription(String topic) {
    }
}
