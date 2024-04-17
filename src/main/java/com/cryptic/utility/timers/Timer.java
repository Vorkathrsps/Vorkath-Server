package com.cryptic.utility.timers;

/**
 * Created by Bart on 8/12/2015.
 */
public class Timer {

    private long key;
    private long ticks;

    public Timer(long key, long ticks) {
        this.key = key;
        this.ticks = ticks;
    }

    public long ticks() {
        return ticks;
    }

    public void ticks(long ticks) {
        this.ticks = ticks;
    }

    public long key() {
        return key;
    }

    public void tick() {
        if (ticks > 0)
            ticks--;
    }

}
