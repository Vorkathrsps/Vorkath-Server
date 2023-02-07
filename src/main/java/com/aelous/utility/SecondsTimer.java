package com.aelous.utility;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

/**
 * Represents a timer in seconds.
 * @author Professor Oak
 */
public class SecondsTimer {

    /**
     * The amount of seconds to count down.
     */
    private int seconds;

    /**
     * The actual timer.
     */
    private final Stopwatch stopwatch;

    public boolean started, completed;

    /**
     * Constructs a new timer.
     */
    public SecondsTimer() {
        this.stopwatch = Stopwatch.createUnstarted();
        this.seconds = 0;
    }

    public boolean running() {
        return stopwatch.isRunning();
    }

    /**
     * Constructs a new timer and
     * starts it immediately.
     *
     * @param seconds The amount of seconds to
     */
    public SecondsTimer(int seconds) {
        this();
        start(seconds);
    }

    /**
     * Starts this timer.
     *
     * @param seconds The amount of seconds.
     */
    public void start(int seconds) {
        this.seconds = seconds;

        //Reset and then start the stopwatch.
        stopwatch.reset();
        stopwatch.start();
        started = true;
        completed = false; // fresh logic
    }

    /**
     * Stops this timer
     */
    public void stop() {
        seconds = 0;
        if (stopwatch.isRunning()) {
            stopwatch.reset();
        }
        started = false;
    }

    /**
     * Gets the amount of seconds remaining
     * before this timer has reached 0.
     *
     * @return The seconds remaining, cant go below 0
     */
    public int secondsRemaining() {
        if (seconds == 0) {
            return 0;
        }
        int remaining = seconds - secondsElapsed();
        if (remaining < 0) {
            remaining = 0;
        }
        return remaining;
    }

    @Deprecated
    /**
     * @deprecated DONT USE THIS
     */
    public boolean finished() {
        throw new RuntimeException("this has a double meaning and cannot be used");
    }

    /**
     * like oss extendOrRegister, just checks if timer is still active (has been started)
     * @return
     */
    public boolean active() {
        return secondsRemaining() > 0;
    }

    /**
     * Gets the amount of seconds that have elapsed
     * since the timer was started.
     *
     * @return The seconds elapsed.
     */
    public int secondsElapsed() {
        return (int) stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000;
    }

    @Override
    public String toString() {
        return "SecondsTimer{" +
            "remain=" + secondsRemaining() +
            '}';
    }

    /**
     * a one-time trigger that will be true when the task has been run, and has then completed. in oss this is in cycle() when timer hits 0. in luxoz, you have to manually call this check to detect when its "finished" after ACTUALLY BEING RUN.
     * @return
     */
    public boolean expiredAfterBeingRun() {
        final boolean yes = started && secondsRemaining() == 0;
        if (yes) {
            completed = true;
            stop(); // important this now goes false, equivilent in OSS of removing from the TimerKey map
        }
        return completed;
    }
}
