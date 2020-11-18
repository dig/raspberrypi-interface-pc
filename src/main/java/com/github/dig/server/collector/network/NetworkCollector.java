package com.github.dig.server.collector.network;

import com.github.dig.server.collector.Collector;
import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.NetworkIF;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class NetworkCollector extends Collector {

    protected NetworkIF network;

    public NetworkCollector(@NonNull SystemInfo systemInfo, int networkCardId) {
        List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
        if (networkIFs.size() > networkCardId) {
            this.network = networkIFs.get(networkCardId);
        }
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(10);
    }
}
