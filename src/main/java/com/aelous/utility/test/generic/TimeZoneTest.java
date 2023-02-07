package com.aelous.utility.test.generic;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneTest {

    public static void main(String[] args) {
        final TimeZone timeZone = Calendar.getInstance().getTimeZone();
        System.out.println("default tz "+timeZone);

        final String testTz = "UTC-5";
        final ZoneId of     = ZoneId.of(testTz);
        System.out.println("java.time zone "+of);

        final ZonedDateTime now = ZonedDateTime.now();
        System.out.println("java.time now "+now);

        final ZonedDateTime now1 = ZonedDateTime.now(of);
        System.out.println("java.time now for TZ "+now1);

        final Date time = Calendar.getInstance().getTime();
        System.out.println("Calendar: "+time);

        final Calendar instance = Calendar.getInstance();
        final TimeZone timeZone1 = TimeZone.getTimeZone("UTC-5:00");
        instance.setTimeZone(timeZone1);
        System.out.println("calendar adjusted: "+instance.getTime()+" for "+timeZone1);

        System.out.println("locale "+Locale.getDefault());
    }
}
