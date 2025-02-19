package ru.salnikov.ssesubscriptiondemo.test.util;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static ru.salnikov.ssesubscriptiondemo.test.base.TestContainerInitializer.KAFKA_CONTAINER;

/**
 * Утилитарный класс для настройки тестовой кафки.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@UtilityClass
public class KafkaTestUtil {

    public static void createTopics(String... topics) {
        try (
                var adminClient = AdminClient.create(
                        Map.of(BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers())
                )
        ) {
            adminClient.createTopics(
                    stringsToNewTopics(topics)
            );
        }
    }

    private static List<NewTopic> stringsToNewTopics(String... topics) {
        return stream(topics).map(KafkaTestUtil::stringToNewTopic)
                .toList();
    }

    private static NewTopic stringToNewTopic(String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

}
