package tech.brtrndb.nats;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

@Slf4j
public class NatsContainer extends GenericContainer<NatsContainer> {

    private static final String IMAGE_VERSION = "nats:latest";
    private static final int NATS_PORT = 4222;
    private static final int NATS_PORT_MONITORING = 8222;
    private static final String NATS_AUTH = "test";

    private static NatsContainer container = null;

    public NatsContainer() {
        super(IMAGE_VERSION);
    }

    public void withLog() {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log).withSeparateOutputStreams();
        this.followOutput(logConsumer);
    }

    public String getNatsUrl() {
        return "nats://" + NATS_AUTH + "@" + NatsContainer.getInstance().getContainerIpAddress() + ":" + this.getMappedPort(NATS_PORT);
    }

    public static NatsContainer getInstance() {
        if (Objects.isNull(container)) {
            container = new NatsContainer()
                    .withExposedPorts(NATS_PORT, NATS_PORT_MONITORING)
                    .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"))
                    .withCommand("-DV --auth " + NATS_AUTH + " -p " + NATS_PORT + " -m " + NATS_PORT_MONITORING);
        }
        return container;
    }

}
