package com.github.dig.server.collector.network;

import lombok.NonNull;
import oshi.SystemInfo;

public class NetworkBytesSentCollector extends NetworkCollector {

    public NetworkBytesSentCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "networkBytesSent";
    }

    @Override
    public String collect() {
        return String.valueOf(network == null ? 0 : network.getBytesSent());
    }
}
