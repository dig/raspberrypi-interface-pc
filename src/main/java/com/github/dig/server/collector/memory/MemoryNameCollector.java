package com.github.dig.server.collector.memory;

import lombok.NonNull;
import oshi.SystemInfo;

public class MemoryNameCollector extends MemoryCollector {

    private final static String DEFAULT_NAME = "Generic Memory";

    public MemoryNameCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "memoryName";
    }

    @Override
    public String collect() {
        String name = DEFAULT_NAME;
        if (memory.getPhysicalMemory().size() > 0) {
            name += " " + memory.getPhysicalMemory().get(0).getMemoryType();
        }
        return name;
    }

    @Override
    protected long interval() {
        return 0;
    }
}
