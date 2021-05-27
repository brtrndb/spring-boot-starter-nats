package tech.brtrndb.nats;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tech.brtrndb.nats.autoconfigure.NatsAutoConfiguration;

@Slf4j
@Testcontainers
public abstract class NatsBaseTest {

    @Container
    public static final NatsContainer NATS_CONTAINER = NatsContainer.getInstance();

    protected final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NatsAutoConfiguration.class));
    protected final String defaultConnectionName = "connection-test";
    protected final int defaultDurationInSec = 5;

    @BeforeAll
    public static void beforeAll() {
        log.debug("Starting container...");
        NATS_CONTAINER.withLog(false);
        NATS_CONTAINER.start();
        log.info("Container started: {}.", NATS_CONTAINER.getContainerId());
        log.info("Container url: {}.", NATS_CONTAINER.getNatsUrl());
        log.info("Nats server is ready.");
    }

    protected ApplicationContextRunner setupDefaultContext() {
        return this.contextRunner.withPropertyValues(
                "spring.nats.server=" + NATS_CONTAINER.getNatsUrl(),
                "spring.nats.connection-name=" + defaultConnectionName,
                "spring.nats.connection-timeout=" + defaultDurationInSec + "s");
    }

    /**/
}
