package com.github.dig.server.collector.disk;

import com.github.dig.server.collector.Collector;
import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DiskCollector extends Collector {

    protected HWDiskStore diskStore;

    public DiskCollector(@NonNull SystemInfo systemInfo, int diskId) {
        List<HWDiskStore> diskStores = systemInfo.getHardware().getDiskStores();
        if (diskStores.size() > diskId) {
            this.diskStore = diskStores.get(diskId);
        }
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(1);
    }
}