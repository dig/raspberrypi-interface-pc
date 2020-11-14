package com.github.dig.server.collector.memory;

import lombok.NonNull;
import oshi.SystemInfo;

public class MemoryAvailableCollector extends MemoryCollector {

    public MemoryAvailableCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "memoryAvailable";
    }

    @Override
    public String collect() {
        return String.valueOf(memory.getAvailable());
    }
}
