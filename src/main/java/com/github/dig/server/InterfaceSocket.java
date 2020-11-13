package com.github.dig.server;

import lombok.extern.java.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

@Log
public class InterfaceSocket extends WebSocketClient {

    private final InterfaceApp interfaceApp = InterfaceApp.getInstance();

    public InterfaceSocket(URI serverUri) {
        super(serverUri);
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
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }
}
