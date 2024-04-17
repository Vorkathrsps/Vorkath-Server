package com.cryptic.utility.timers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimerRepository {
    private long keyMask = 0L;
    private final long[] timers = new long[TimerKey.cachedValues.length];

    public List<TimerKey> getActiveTimers() {
        List<TimerKey> activeTimers = new ArrayList<>();
        TimerKey[] keys = TimerKey.values();
        for (int i = 0; i < keys.length; i++) {
            if ((keyMask & (1L << i)) != 0) {
                activeTimers.add(keys[i]);
            }
        }
        return activeTimers;
    }

    public boolean has(TimerKey key) {
        return (keyMask & (1L << key.ordinal())) != 0;
    }

    public void register(TimerKey key, int ticks) {
        int index = key.ordinal();
        keyMask |= (1L << index);
        timers[index] = ticks;
    }

    public int left(TimerKey key) {
        int index = key.ordinal();
        if ((keyMask & (1L << index)) != 0) return (int) timers[index];
        return 0;
    }

    public void extendOrRegister(TimerKey key, int ticks) {
        int index = key.ordinal();
        long timerFlags = this.keyMask;
        boolean timerExists = (timerFlags & (1L << index)) != 0;
        if (!timerExists) {
            timers[index] = ticks;
            timerFlags |= (1L << index);
        } else {
            timers[index] = Math.max(timers[index], ticks);
        }
        this.keyMask = timerFlags;
    }

    public void addOrSet(TimerKey key, int ticks) {
        int index = key.ordinal();
        long timerFlags = this.keyMask;
        boolean timerExists = (timerFlags & (1L << index)) != 0;
        if (!timerExists) {
            timers[index] = ticks;
            timerFlags |= (1L << index);
        } else {
            timers[index] += ticks;
        }
        this.keyMask = timerFlags;
    }

    public void cancel(TimerKey key) {
        int index = key.ordinal();
        long timerFlags = this.keyMask;
        boolean timerExists = (timerFlags & (1L << index)) != 0;
        if (timerExists) {
            timerFlags &= ~(1L << index);
            timers[index] = 0;
            this.keyMask = timerFlags;
        }
    }

    public void cycle() {
        if (keyMask == 0L) return;
        int numTimerKeys = TimerKey.values().length;
        for (int i = 0; i < numTimerKeys; i++) {
            if ((keyMask & (1L << i)) != 0) {
                timers[i]--;
                if (timers[i] <= 0) {
                    keyMask &= ~(1L << i);
                    timers[i] = 0;
                }
            }
        }
    }

    public String asMinutesAndSecondsLeft(TimerKey key) {
        long ms = left(key) * 600L;
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(ms);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(ms) % 60;
        String str = "";

        if (minutes > 0) {
            if (minutes > 1) {
                str = minutes + " minutes";
            } else {
                str = "one minute";
            }

            if (seconds > 0) {
                str += " and ";
            }
        }

        if (seconds == 1) {
            str += "one second";
        } else if (seconds > 0) {
            str += seconds + " seconds";
        }

        return str;
    }

    public String asMinutesLeft(TimerKey key) {
        long ms = left(key) * 600L;
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(ms);
        String str = "";

        if (minutes > 0) {
            if (minutes > 1) {
                str = minutes + " mins";
            } else {
                str = "One min";
            }
        }

        return str;
    }

    public String asSeconds(TimerKey key) {
        long ms = left(key) * 600L;
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(ms);
        String str = "";


        if (seconds > 1) {
            str = seconds + " seconds";
        } else {
            str = "One second";
        }

        return str;
    }

}
