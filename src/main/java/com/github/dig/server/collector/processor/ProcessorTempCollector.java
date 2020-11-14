package com.github.dig.server.collector.processor;

import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.Sensors;

public class ProcessorTempCollector extends ProcessorCollector {

    private final Sensors sensors;
    public ProcessorTempCollector(@NonNull SystemInfo systemInfo) {
        super(systemInfo);
        this.sensors = systemInfo.getHardware().getSensors();
    }

    @Override
    public String getKey() {
        return "processorTemp";
    }

    @Override
    public String collect() {
        return String.valueOf(sensors.getCpuTemperature());
    }
}
