package com.github.dig.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;

@Log
public class InterfaceSocket extends WebSocketClient {

    private final String authKey;
    public InterfaceSocket(@NonNull URI serverUri, @NonNull String authKey) {
        super(serverUri);
        this.authKey = authKey;
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onError(Exception ex) {
        log.log(Level.SEVERE, "Socket exception", ex);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("authenticate", authKey, String.valueOf(ClientType.SOURCE.getId()));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    public void send(@NonNull String event, @NonNull String... value) {
        send(String.format("%s;%s", event, String.join(";", value)));
    }

    @Getter
    private enum ClientType {

        SOURCE(0),
        RECEIVER(1);

        private int id;
        ClientType(int id) {
            this.id = id;
        }
    }
}
