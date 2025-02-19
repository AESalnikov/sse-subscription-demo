package ru.salnikov.ssesubscriptiondemo.test.configuration;

import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;

import java.util.Map;
import java.util.UUID;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static ru.salnikov.ssesubscriptiondemo.test.base.TestContainerInitializer.KAFKA_CONTAINER;

/**
 * Конфигурация для тестов.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@TestConfiguration
public class TestEventConfiguration {

    @Bean
    public KafkaTemplate<UUID, EventDto> kafkaTestTemplate(ProducerFactory<UUID, EventDto> kafkaTestProducerFactory) {
        return new KafkaTemplate<>(kafkaTestProducerFactory);
    }

    @Bean
    public ProducerFactory<UUID, EventDto> kafkaTestProducerFactory(Map<String, Object> propertiesTest) {
        return new DefaultKafkaProducerFactory<>(
                propertiesTest,
                new UUIDSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public Map<String, Object> propertiesTest() {
        return Map.of(BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
    }

}
