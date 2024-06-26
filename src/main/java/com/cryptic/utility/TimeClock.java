package com.cryptic.utility;

public class TimeClock {

    private long startTime;

    public TimeClock() {
        this.startTime = System.currentTimeMillis();
    }

    public String currentTimeClock() {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - startTime;
        long hours = elapsedMillis / (1000 * 60 * 60);
        long minutes = (elapsedMillis / (1000 * 60)) % 60;
        long seconds = (elapsedMillis / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

}
