package com.github.dig.server.collector.processor;

import com.github.dig.server.collector.Collector;
import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.concurrent.TimeUnit;

public abstract class ProcessorCollector extends Collector {

    protected final CentralProcessor processor;

    public ProcessorCollector(@NonNull SystemInfo systemInfo) {
        this.processor = systemInfo.getHardware().getProcessor();
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(1);
    }
}
