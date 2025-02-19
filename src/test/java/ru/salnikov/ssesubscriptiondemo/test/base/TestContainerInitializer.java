package ru.salnikov.ssesubscriptiondemo.test.base;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.utility.DockerImageName.parse;

/**
 * Инициализация тест-контейнеров.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
@Testcontainers
public interface TestContainerInitializer {

    @Container
    MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(parse("mongo:8.0.0"));
    @Container
    KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.2"));

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

}
