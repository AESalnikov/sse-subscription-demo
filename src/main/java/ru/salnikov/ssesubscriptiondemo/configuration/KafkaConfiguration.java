package ru.salnikov.ssesubscriptiondemo.configuration;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.salnikov.ssesubscriptiondemo.configuration.property.KafkaStreamsProperty;
import ru.salnikov.ssesubscriptiondemo.domain.dto.EventDto;

import java.util.Map;
import java.util.UUID;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME;

/**
 * Общая конфигурация кафки.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Configuration
@AllArgsConstructor
public class KafkaConfiguration {


    private static final Serde<UUID> KEY_SERDE = Serdes.UUID();
    private static final Serde<EventDto> VALUE_SERDE = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(EventDto.class));

    private KafkaStreamsProperty kafkaStreamsProperty;

    @Bean(name = DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(Map<String, Object> kafkaStreamsProperties) {
        return new KafkaStreamsConfiguration(kafkaStreamsProperties);
    }

    @Bean
    public Map<String, Object> kafkaStreamsProperties() {
        return Map.of(
                APPLICATION_ID_CONFIG, kafkaStreamsProperty.applicationId(),
                BOOTSTRAP_SERVERS_CONFIG, kafkaStreamsProperty.bootstrapServers(),
                DEFAULT_KEY_SERDE_CLASS_CONFIG, KEY_SERDE.getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, VALUE_SERDE.getClass().getName()
        );
    }

    @Bean
    public KStream<UUID, EventDto> eventKafkaStream(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(kafkaStreamsProperty.subscription().topic(), Consumed.with(KEY_SERDE, VALUE_SERDE));
    }

}
