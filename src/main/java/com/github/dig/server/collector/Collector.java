package com.github.dig.server.collector;

public abstract class Collector {

    protected long updateMs;
    protected int count;
    public Collector() {
        this.updateMs = 0;
        this.count = 0;
    }

    public abstract String getKey();

    public abstract String collect();

    protected abstract long interval();

    public boolean canCollect() {
        boolean canNextUpdate = this.updateMs < System.currentTimeMillis();

        if ((interval() > 0 && canNextUpdate) || (interval() <= 0 && this.count <= 0)) {
            this.updateMs = System.currentTimeMillis() + interval();
            this.count++;
            return true;
        }

        return false;
    }

    protected double roundTwoDp(double input) {
        return Math.round(input * 100.0) / 100.0;
    }
}
