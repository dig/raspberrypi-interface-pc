package com.github.dig.server.collector.gpu;

import com.github.dig.server.collector.Collector;
import lombok.NonNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class GpuCollector extends Collector {

    protected GraphicsCard graphicsCard;

    public GpuCollector(@NonNull SystemInfo systemInfo) {
        List<GraphicsCard> graphicsCards = systemInfo.getHardware().getGraphicsCards();
        if (graphicsCards.size() > 0) {
            this.graphicsCard = graphicsCards.get(0);
        }
    }

    @Override
    protected long interval() {
        return TimeUnit.SECONDS.toMillis(1);
    }
}

