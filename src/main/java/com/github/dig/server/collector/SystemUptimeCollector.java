package com.github.dig.server.collector;

import lombok.NonNull;
import oshi.software.os.OperatingSystem;

public class SystemUptimeCollector extends Collector {

    private final OperatingSystem os;
    public SystemUptimeCollector(@NonNull OperatingSystem os) {
        this.os = os;
    }

    @Override
    public String getKey() {
        return "systemUptime";
    }

    @Override
    public String collect() {
        return String.valueOf(os.getSystemUptime());
    }

    @Override
    protected long interval() {
        return 0;
    }
}
