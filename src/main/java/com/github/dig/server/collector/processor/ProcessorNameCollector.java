package com.github.dig.server.collector.processor;

import lombok.NonNull;
import oshi.SystemInfo;

public class ProcessorNameCollector extends ProcessorCollector {

    public ProcessorNameCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
    }

    @Override
    public String getKey() {
        return "processorName";
    }

    @Override
    public String collect() {
        return processor.getProcessorIdentifier().getName();
    }

    @Override
    protected long interval() {
        return 0;
    }
}
