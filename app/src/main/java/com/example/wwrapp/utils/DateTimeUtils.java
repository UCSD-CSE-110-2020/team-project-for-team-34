package com.example.wwrapp.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Sources:
 * https://www.logicbig.com/how-to/java-8-date-time-api/millis-to-date.html
 */
public class DateTimeUtils {

    public static long getMillisFromDateTime(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        long millis = zdt.toInstant().toEpochMilli();
        return millis;
    }

    public static LocalDateTime getDateTimeFromMillis(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }
}
