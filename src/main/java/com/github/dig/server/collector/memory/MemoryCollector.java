package com.github.dig.server.collector.memory;

import com.github.dig.server.collector.Collector;
import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.util.concurrent.TimeUnit;

public abstract class MemoryCollector extends Collector {

    protected final GlobalMemory memory;

    public MemoryCollector(@NonNull SystemInfo systemInfo) {
        this.memory = systemInfo.getHardware().getMemory();
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(1);
    }
}