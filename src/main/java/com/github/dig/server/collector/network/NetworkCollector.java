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

    public NetworkCollector(@NonNull SystemInfo systemInfo) {
        List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
        if (networkIFs.size() > 0) {
            this.network = networkIFs.get(0);
        }
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(1);
    }
}
