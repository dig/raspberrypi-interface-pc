package com.github.dig.server.collector.disk;

import lombok.NonNull;
import oshi.SystemInfo;

public class DiskNameCollector extends DiskCollector {

    private final static String UNKNOWN_DISK = "Unknown";

    public DiskNameCollector(@NonNull SystemInfo systemInfo, int diskId) {
        super(systemInfo, diskId);
    }

    @Override
    public String getKey() {
        return "diskName";
    }

    @Override
    public String collect() {
        return diskStore == null ? UNKNOWN_DISK : diskStore.getModel();
    }

    @Override
    protected long interval() {
        return 0;
    }
}
