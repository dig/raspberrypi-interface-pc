package com.github.dig.server.collector.network;

import lombok.NonNull;
import oshi.SystemInfo;

public class NetworkBytesRecvCollector extends NetworkCollector {

    public NetworkBytesRecvCollector(@NonNull SystemInfo systemInfo, int networkCardId) {
        super(systemInfo, networkCardId);
    }

    @Override
    public String getKey() {
        return "networkBytesRecv";
    }

    @Override
    public String collect() {
        network.updateAttributes();
        return String.valueOf(network == null ? 0 : network.getBytesRecv());
    }
}
