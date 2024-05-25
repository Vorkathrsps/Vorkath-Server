package com.cryptic.model.content.events;

public enum TickToSeconds {
    ONE_MINUTE(100),
    NINE_MINUTES(450),
    FIFTEEN_MINUTES(900),
    THIRTY_MINUTES(3_000),
    ONE_HOUR(6_000);
    final int seconds;
    TickToSeconds(int seconds) {
        this.seconds = seconds;
    }

    public static int get(TickToSeconds time) {
        return time.seconds;
    }
}
