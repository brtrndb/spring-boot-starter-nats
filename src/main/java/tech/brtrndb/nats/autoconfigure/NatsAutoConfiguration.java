package tech.brtrndb.nats.autoconfigure;

import java.io.IOException;
import java.util.Objects;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import tech.brtrndb.nats.NatsConfiguration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for NATS.
 * {@link NatsAutoConfiguration} will create a NATS connection from an instance of {@link NatsConfiguration}.
 * A default connection and error handler is provided with basic logging.
 */
@Slf4j
@ConditionalOnClass({Connection.class})
@EnableConfigurationProperties(NatsConfiguration.class)
public class NatsAutoConfiguration {

    /**
     * @return NATS connection created with the provided properties. If no server URL is set, the method will return null.
     * @throws IOException          when a connection error occurs.
     * @throws InterruptedException in the unusual case of a thread interruption during connection.
     */
    @Bean
    @ConditionalOnMissingBean
    public Connection connection(NatsConfiguration natsConfiguration, ConnectionListener connectionListener, ErrorListener errorListener) throws IOException, InterruptedException {
        Objects.requireNonNull(natsConfiguration, "NATS properties should not be null");

        String server = natsConfiguration.getServer().strip();

        if (server.isEmpty()) {
            throw new IllegalArgumentException("NATS server url should not be empty");
        }

        log.debug("NATS will be configured with properties: {}.", natsConfiguration);
        Options options = natsConfiguration.toOptionsBuilder()
                .connectionListener(connectionListener)
                .errorListener(errorListener)
                .build();

        log.trace("Trying to connect to {}.", server);
        Connection nc = Nats.connect(options);
        log.info("Successfully connected to {} as {}.", server, options.getConnectionName());
        return nc;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionListener connectionListener() {
        return new DefaultConnectionListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorListener errorListener() {
        return new DefaultErrorListener();
    }

    /**/
}
