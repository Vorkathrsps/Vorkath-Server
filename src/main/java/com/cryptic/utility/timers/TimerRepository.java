package com.cryptic.utility.timers;

import com.google.common.base.Stopwatch;
import com.cryptic.model.entity.Entity;
import com.cryptic.utility.NpcPerformance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bart on 8/12/2015.
 */
public class TimerRepository {

    protected Map<TimerKey, Timer> timers = new HashMap<>();

    public boolean has(TimerKey key) {
        Timer timer = timers.get(key);
        return timer != null && timer.ticks() > 0;
    }

    public void register(Timer timer) {
        timers.put(timer.key(), timer);
    }

    public int left(TimerKey key) {
        Timer timer = timers.get(key);
        return timer == null ? 0 : timer.ticks();
    }

    public String asHoursAndMinutesLeft(TimerKey key) {
        long ms = left(key) * 600L;
        int hours = (int) TimeUnit.MILLISECONDS.toHours(ms);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(ms);
        String str = "";

        if (hours > 0) {
            if (hours > 1) {
                str = hours + " hours";
            } else {
                str = "one hour";
            }

            if (minutes > 0) {
                str += " and ";
            }
        }

        if (minutes == 1) {
            str += "one minute";
        } else if (minutes > 0) {
            str += minutes + " minutes";
        }

        return str;
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

    public void register(TimerKey key, int ticks) {
        timers.put(key, new Timer(key, ticks));
    }

    /**
     * Extend up to (if exists) the given ticks, or register new
     */
    public void extendOrRegister(TimerKey key, int ticks) {
        Timer t = timers.get(key);
        if (t == null) {
            t = new Timer(key, ticks);
        } else if (t.ticks() < ticks) {
            t.ticks(ticks);
        }
        timers.put(key, t);
    }

    /**
     * Register if non-existant, or extend.
     */
    public void addOrSet(TimerKey key, int ticks) {
        Timer t = timers.get(key);
        if (t == null) {
            t = new Timer(key, ticks);
        } else {
            t.ticks(t.ticks() + ticks);
        }
        timers.put(key, t);
    }

    public void cancel(TimerKey name) {
        timers.put(name, null);
    }

    public void cycle() {
        if (timers.isEmpty()) return;
        Iterator<Map.Entry<TimerKey, Timer>> iter = timers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<TimerKey, Timer> entry = iter.next();
            var timer = entry.getValue();
            if (timer != null) {
                timer.tick();
                if (timer.ticks() == 0)
                    iter.remove();
            }
        }
    }

    private void cyclePerformanceMode(Entity entity) {
       // long lol = 0L;
        timers.forEach((timer, entry) -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            entry.tick();
            stopwatch.stop();

            long ns = stopwatch.elapsed().toNanos();
          //  lol += ns;
            if (NpcPerformance.DETAL_LOG_ENABLED) {
                if (ns > 100_000) { // 0.1ms
                    if (entity.isNpc()) {
                        NpcPerformance.TimerPerfEntry e = new NpcPerformance.TimerPerfEntry();
                        e.name = entry.key();
                        e.duration = stopwatch.elapsed();
                        entity.getAsNpc().performance.addTimerOffender(e);
                        entity.getAsNpc().performance.timers++;
                    }
                }
            }
        });
        /*if (entity.isNpc()) {
            entity.getAsNpc().performance.sumRuntimeTimers = Duration.ofNanos(lol);
        }*/
    }

}
