package com.github.dig.server.collector.processor;

import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ProcessorUsageCollector extends ProcessorCollector {

    private long[] oldTicks;
    public ProcessorUsageCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
        this.oldTicks = new long[CentralProcessor.TickType.values().length];
    }

    @Override
    public String getKey() {
        return "processorUsage";
    }

    @Override
    public String collect() {
        double d = processor.getSystemCpuLoadBetweenTicks(oldTicks);
        oldTicks = processor.getSystemCpuLoadTicks();
        return String.valueOf(roundTwoDp(d * 100));
    }
}
