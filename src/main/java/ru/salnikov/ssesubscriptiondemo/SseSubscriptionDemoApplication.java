package ru.salnikov.ssesubscriptiondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Основной класс приложения.
 */
@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = "ru.salnikov.ssesubscriptiondemo.configuration.property")
public class SseSubscriptionDemoApplication {

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(SseSubscriptionDemoApplication.class, args);
    }

}
