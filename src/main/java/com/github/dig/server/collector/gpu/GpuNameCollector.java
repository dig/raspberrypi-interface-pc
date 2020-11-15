package com.github.dig.server.collector.gpu;

import lombok.NonNull;
import oshi.SystemInfo;

public class GpuNameCollector extends GpuCollector {

    private final static String UNKNOWN_GPU = "Unknown";

    public GpuNameCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "gpuName";
    }

    @Override
    public String collect() {
        return graphicsCard == null ? UNKNOWN_GPU : graphicsCard.getName();
    }

    @Override
    protected long interval() {
        return 0;
    }
}
