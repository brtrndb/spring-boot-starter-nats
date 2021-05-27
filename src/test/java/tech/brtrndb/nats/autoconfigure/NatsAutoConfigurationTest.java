package tech.brtrndb.nats.autoconfigure;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.brtrndb.nats.NatsBaseTest;

@Slf4j
public class NatsAutoConfigurationTest extends NatsBaseTest {

    @Test
    @DisplayName("Should connect to server given provided configuration.")
    public void connection_to_server() {
        this.setupDefaultContext()
                .run(context -> {
                    // Given:
                    // When:
                    Connection connection = context.getBean(Connection.class);

                    // Then:
                    Assertions.assertThat(connection).isNotNull();
                    Assertions.assertThat(connection.getStatus()).isEqualTo(Connection.Status.CONNECTED);
                    Assertions.assertThat(connection.getConnectedUrl()).isEqualTo(NATS_CONTAINER.getNatsUrl());
                    Assertions.assertThat(connection.getOptions())
                            .satisfies(options -> {
                                Assertions.assertThat(options.getConnectionName()).matches(Pattern.compile(defaultConnectionName + "-[0-9]{13}"));
                                Assertions.assertThat(options.getConnectionTimeout()).isEqualTo(Duration.ofSeconds(defaultDurationInSec));
                            });
                });
    }

    @Test
    @DisplayName("Should not be able to connect to server given invalid URL.")
    public void connection_to_server_failed() {
        Assertions.assertThatThrownBy(() -> this.contextRunner.withPropertyValues(
                "spring.nats.server=nats://fake_auth@fake_url:4222",
                "spring.nats.connection-name=" + defaultConnectionName,
                "spring.nats.connection-timeout=" + defaultDurationInSec + "s")
                .run(context -> {
                    // Should not be able to connect to server.
                    Connection connection = context.getBean(Connection.class);
                }))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasRootCauseExactlyInstanceOf(IOException.class);
    }

    /**/
}
