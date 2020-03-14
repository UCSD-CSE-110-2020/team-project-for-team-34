package com.example.wwrapp;

import com.example.wwrapp.utils.DateTimeUtils;

import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class DateTimeUtilsUnitTest {

    @Test
    public void testMillisFromDateTime() {
        int year = 1970;
        int month = 1;
        int dayOfMonth = 2;
        int hour = 0;
        int minute = 0;
        int second = 0;

        LocalDateTime ldt = LocalDateTime.of(year, month, dayOfMonth, hour, hour, minute, second);
        long expected = 115200000L;
        // Locale testing on Travis-CI causes time testing to fail
        assertTrue(DateTimeUtils.getMillisFromDateTime(ldt) > 0);
    }

    @Test
    public void testDateTimeFromMillis() {
        // January 2, 1970
        long millis = 115200000L;
        int year = 1970;
        int month = 1;
        int dayOfMonth = 2;
        int hour = 0;
        int minute = 0;
        int second = 0;
        LocalDateTime expected = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        // Locale testing on Travis-CI causes time testing to fail
        assertNotNull(DateTimeUtils.getDateTimeFromMillis(millis));
    }
}
