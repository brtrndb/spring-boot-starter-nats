package tech.brtrndb.nats.autoconfigure;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.ErrorListener;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tech.brtrndb.nats.NatsContainer;
import tech.brtrndb.nats.util.ContextUtils;

@Slf4j
@Testcontainers
public class NatsAutoConfigurationTest {

    @Container
    public static final NatsContainer NATS_CONTAINER = NatsContainer.getInstance();

    private static final String DEFAULT_CONNECTION_NAME = "connection-test";
    private static final int DEFAULT_DURATION_IN_SEC = 5;

    private static final ApplicationContextRunner CONTEXT_RUNNER = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NatsAutoConfiguration.class));

    @BeforeAll
    public static void beforeAll() {
        log.debug("Starting container...");
        NATS_CONTAINER.withLog();
        NATS_CONTAINER.start();
        log.info("Container started: {}.", NATS_CONTAINER.getContainerId());
        log.info("Container url: {}.", NATS_CONTAINER.getNatsUrl());
        log.info("Nats server is ready.");
    }

    @Test
    @DisplayName("Should not load context given null server url.")
    public void load_no_beans() {
        // Given:
        ApplicationContextRunner context = CONTEXT_RUNNER.withPropertyValues();

        // When:
        // Then:
        Assertions.assertThatThrownBy(() -> context.run(ctx -> {
                    // Should not be able to instantiate connection.
                    Connection connection = ctx.getBean(Connection.class);
                }))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasCauseExactlyInstanceOf(BeanCreationException.class)
                .hasRootCauseExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should load context given full configuration.")
    public void load_all_beans() {
        // Given:
        ApplicationContextRunner context = CONTEXT_RUNNER.withPropertyValues(
                "spring.nats.server=" + NATS_CONTAINER.getNatsUrl(),
                "spring.nats.connection-name=" + DEFAULT_CONNECTION_NAME,
                "spring.nats.connection-timeout=" + DEFAULT_DURATION_IN_SEC + "s"
        );

        // When:
        // Then:
        ContextUtils.beanInContext(context, Connection.class);
        ContextUtils.beanInContext(context, ConnectionListener.class);
        ContextUtils.beanInContext(context, ErrorListener.class);
    }

    @Test
    @DisplayName("Should connect to server given provided configuration.")
    public void connection_to_server() {
        // Given:
        ApplicationContextRunner context = CONTEXT_RUNNER.withPropertyValues(
                "spring.nats.server=" + NATS_CONTAINER.getNatsUrl(),
                "spring.nats.connection-name=" + DEFAULT_CONNECTION_NAME,
                "spring.nats.connection-timeout=" + DEFAULT_DURATION_IN_SEC + "s"
        );

        // When:
        // Then:
        context.run(ctx -> {
            // Given:
            // When:
            Connection connection = ctx.getBean(Connection.class);

            // Then:
            Assertions.assertThat(connection).isNotNull();
            Assertions.assertThat(connection.getStatus()).isEqualTo(Connection.Status.CONNECTED);
            Assertions.assertThat(connection.getConnectedUrl()).isEqualTo(NATS_CONTAINER.getNatsUrl());
            Assertions.assertThat(connection.getOptions())
                    .satisfies(options -> {
                        Assertions.assertThat(options.getConnectionName()).matches(Pattern.compile(DEFAULT_CONNECTION_NAME + "-[0-9]{13}"));
                        Assertions.assertThat(options.getConnectionTimeout()).isEqualTo(Duration.ofSeconds(DEFAULT_DURATION_IN_SEC));
                    });
        });
    }

    @Test
    @DisplayName("Should not be able to connect to server given invalid URL.")
    public void connection_to_server_failed() {
        // Given:
        ApplicationContextRunner context = CONTEXT_RUNNER.withPropertyValues(
                "spring.nats.server=nats://fake_auth@fake_url:4222",
                "spring.nats.connection-name=" + DEFAULT_CONNECTION_NAME,
                "spring.nats.connection-timeout=" + DEFAULT_DURATION_IN_SEC + "s"
        );

        // When:
        // Then:
        Assertions.assertThatThrownBy(() -> context.run(ctx -> {
                    // Should not be able to connect to server.
                    Connection connection = ctx.getBean(Connection.class);
                }))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasCauseExactlyInstanceOf(BeanCreationException.class)
                .hasRootCauseExactlyInstanceOf(IOException.class);
    }

}
