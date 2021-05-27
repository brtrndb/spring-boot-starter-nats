package tech.brtrndb.nats;

import java.time.Duration;
import java.time.Instant;

import io.nats.client.Options;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "spring.nats")
public class NatsConfiguration {

    /**
     * NATS server url.
     */
    private String server = Options.DEFAULT_URL;

    /**
     * Connection name, shows up in thread names.
     */
    private String connectionName;

    /**
     * Time between pings to the server to check "liveness".
     */
    private Duration pingInterval = Options.DEFAULT_PING_INTERVAL;

    /**
     * Shortcut to turn on verbose mode and connection trace logs.
     */
    private boolean verbose = false;

    /**
     * Maximum reconnect attempts if a connection is lost, after the initial connection.
     */
    private int maxReconnects = Options.DEFAULT_MAX_RECONNECT;

    /**
     * Time to wait between reconnect attempts to the same server url.
     */
    private Duration reconnectWait = Options.DEFAULT_RECONNECT_WAIT;

    /**
     * Timeout for the initial connection, if this time is passed, the connection will fail and no reconnect attempts are made.
     */
    private Duration connectionTimeout = Options.DEFAULT_CONNECTION_TIMEOUT;

    /**
     * Size of the buffer, in bytes, used to hold outgoing messages during reconnect.
     */
    private long reconnectBufferSize = Options.DEFAULT_RECONNECT_BUF_SIZE;

    /**
     * Prefix to use for inboxes, generally the default is used but custom prefixes can allow security controls.
     */
    private String inboxPrefix = Options.DEFAULT_INBOX_PREFIX;

    /**
     * Whether or not the server will send messages sent from this connection back to the connection.
     */
    private boolean noEcho = false;

    /**
     * Whether or not to treat subjects as UTF-8, the default is ASCII.
     */
    private boolean utf8Support = false;

    /**
     * @return NATS options builder based on this set of properties, useful if other settings are required before connect is called.
     */
    public Options.Builder toOptionsBuilder() {
        Options.Builder builder = new Options.Builder()
                .server(this.server)
                .connectionName("%s-%d".formatted(this.connectionName, Instant.now().toEpochMilli()))
                .maxReconnects(this.maxReconnects)
                .reconnectWait(this.reconnectWait)
                .connectionTimeout(this.connectionTimeout)
                .pingInterval(this.pingInterval)
                .reconnectBufferSize(this.reconnectBufferSize)
                .inboxPrefix(this.inboxPrefix);

        if (this.noEcho)
            builder = builder.noEcho();

        if (this.utf8Support)
            builder = builder.supportUTF8Subjects();

        if (this.verbose)
            builder = builder.verbose().traceConnection();

        return builder;
    }

    /**
     * @return NATS options based on this set of properties.
     */
    public Options toOptions() {
        return toOptionsBuilder().build();
    }

    /**/
}
