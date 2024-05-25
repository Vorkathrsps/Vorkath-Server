package com.cryptic.utility;

import java.time.Duration;
import java.time.Instant;

public class TimeClock {
    private final Instant startTime;

    public TimeClock() {
        this.startTime = Instant.now();
    }

    public String currentTimeClock() {
        Instant current = Instant.now();
        Duration duration = Duration.between(startTime, current);
        long hours = duration.toHours();
        long minutes = (duration.toMinutes() % 60);
        long seconds = (duration.getSeconds() % 60);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        TimeClock clock = new TimeClock();
        System.out.println(clock.currentTimeClock());
    }
}
