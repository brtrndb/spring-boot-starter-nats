package tech.brtrndb.nats.autoconfigure;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class DefaultConnectionListener implements ConnectionListener {

    @Override
    public void connectionEvent(Connection connection, Events type) {
        log.info("NATS connection status has changed: {}.", type);
    }

}
