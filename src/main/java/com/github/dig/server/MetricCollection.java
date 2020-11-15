package com.github.dig.server;

import com.github.dig.server.collector.Collector;
import com.github.dig.server.collector.SystemUptimeCollector;
import com.github.dig.server.collector.disk.DiskNameCollector;
import com.github.dig.server.collector.gpu.GpuNameCollector;
import com.github.dig.server.collector.memory.MemoryAvailableCollector;
import com.github.dig.server.collector.memory.MemoryNameCollector;
import com.github.dig.server.collector.memory.MemoryTotalCollector;
import com.github.dig.server.collector.network.NetworkBytesRecvCollector;
import com.github.dig.server.collector.network.NetworkBytesSentCollector;
import com.github.dig.server.collector.processor.ProcessorNameCollector;
import com.github.dig.server.collector.processor.ProcessorTempCollector;
import com.github.dig.server.collector.processor.ProcessorUsageCollector;
import com.google.gson.Gson;
import lombok.NonNull;
import oshi.SystemInfo;

import java.util.*;

public class MetricCollection extends Thread {

    private final static Gson GSON = new Gson();

    private final SystemInfo SYSTEM_INFO = new SystemInfo();
    private final Set<Collector> COLLECTORS = new HashSet<>(Arrays.asList(
            new GpuNameCollector(SYSTEM_INFO),

            new MemoryAvailableCollector(SYSTEM_INFO),
            new MemoryNameCollector(SYSTEM_INFO),
            new MemoryTotalCollector(SYSTEM_INFO),

            new NetworkBytesRecvCollector(SYSTEM_INFO),
            new NetworkBytesSentCollector(SYSTEM_INFO),

            new ProcessorNameCollector(SYSTEM_INFO),
            new ProcessorTempCollector(SYSTEM_INFO),
            new ProcessorUsageCollector(SYSTEM_INFO),

            new SystemUptimeCollector(SYSTEM_INFO.getOperatingSystem())
            ));

    private final Map<String, String> payload;
    private final InterfaceSocket socket;
    private final long delay;

    public MetricCollection(@NonNull InterfaceSocket socket,
                            @NonNull Properties properties) {
        this.payload = new HashMap<>();
        this.socket = socket;
        this.delay = Long.valueOf(properties.getProperty("refresh-time", String.valueOf(Defaults.REFRESH_TIME)));

        int diskId = Integer.valueOf(properties.getProperty("disk-id", String.valueOf(Defaults.DISK_ID)));
        COLLECTORS.add(new DiskNameCollector(SYSTEM_INFO, diskId));
    }

    @Override
    public void run() {
        while (true) {
            payload.clear();
            COLLECTORS.stream()
                    .filter(Collector::canCollect)
                    .forEach(collector -> payload.put(collector.getKey(), collector.collect()));

            if (payload.size() > 0 && socket.isOpen()) {
                socket.send("data", GSON.toJson(payload));
            }

            try {
                sleep(delay);
            } catch (InterruptedException e) {}
        }
    }
}
