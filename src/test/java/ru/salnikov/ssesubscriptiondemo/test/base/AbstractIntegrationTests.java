package ru.salnikov.ssesubscriptiondemo.test.base;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.salnikov.ssesubscriptiondemo.test.configuration.TestEventConfiguration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static ru.salnikov.ssesubscriptiondemo.test.util.KafkaTestUtil.createTopics;

/**
 * Базовый класс для интеграционных тестов.
 */
@ActiveProfiles("test")
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 0)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = TestEventConfiguration.class
)
public abstract class AbstractIntegrationTests implements TestContainerInitializer {

    @Autowired
    protected WebTestClient webClient;

    @BeforeAll
    public static void beforeAll() {
        createTopics("sse-subscription-demo-topic");
    }

}
