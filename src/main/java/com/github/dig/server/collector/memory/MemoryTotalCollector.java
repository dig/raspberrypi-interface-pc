package com.github.dig.server.collector.memory;

import lombok.NonNull;
import oshi.SystemInfo;

public class MemoryTotalCollector extends MemoryCollector {

    public MemoryTotalCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "memoryTotal";
    }

    @Override
    public String collect() {
        return String.valueOf(memory.getTotal());
    }

    @Override
    protected long interval() {
        return 0;
    }
}
