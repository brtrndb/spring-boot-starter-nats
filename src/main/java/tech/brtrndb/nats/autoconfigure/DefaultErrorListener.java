package tech.brtrndb.nats.autoconfigure;

import io.nats.client.Connection;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class DefaultErrorListener implements ErrorListener {

    @Override
    public void slowConsumerDetected(Connection connection, Consumer consumer) {
        log.warn("NATS connection slow consumer detected.");
    }

    @Override
    public void exceptionOccurred(Connection connection, Exception e) {
        log.error("NATS connection exception occurred.", e);
    }

    @Override
    public void errorOccurred(Connection connection, String error) {
        log.error("NATS connection error occurred: {}. ", error);
    }

}
