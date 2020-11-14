package com.github.dig.server;

import com.github.dig.server.collector.Collector;
import com.github.dig.server.collector.processor.ProcessorNameCollector;
import com.github.dig.server.collector.processor.ProcessorTempCollector;
import com.github.dig.server.collector.processor.ProcessorUsageCollector;
import oshi.SystemInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MetricCollection extends Thread {

    private final static SystemInfo SYSTEM_INFO = new SystemInfo();
    private final static Set<Collector> COLLECTORS = new HashSet<>(Arrays.asList(
            new ProcessorNameCollector(SYSTEM_INFO),
            new ProcessorTempCollector(SYSTEM_INFO),
            new ProcessorUsageCollector(SYSTEM_INFO)


            ));

    @Override
    public void run() {
        while (true) {
            for (Collector collector : COLLECTORS) {
                if (collector.canCollect()) {
                    System.out.println(collector.getKey() + " : " + collector.collect());
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}
