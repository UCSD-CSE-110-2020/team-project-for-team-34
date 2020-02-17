package com.example.wwrapp;

import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static junit.framework.TestCase.assertEquals;

public class TimeMachineUnitTest {

    @Test
    public void testTimeMachine() {
        long currentTime = 1581893483403L;
        Instant instant = Instant.ofEpochMilli(currentTime);
        LocalDateTime mockDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        TimeMachine.useFixedClockAt(mockDateTime);

        String dateTimeAsString = "2020-02-16T14:51:23.403";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime expected = LocalDateTime.parse(dateTimeAsString, formatter);
        assertEquals(expected, TimeMachine.now());
    }

    @Test
    public void testClock() {
        long currentTime = 1581893483403L;
        Instant instant = Instant.ofEpochMilli(currentTime);
        LocalDateTime mockDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        TimeMachine.useFixedClockAt(mockDateTime);

        Clock current = TimeMachine.getClock();
        System.out.println(current.millis());

        Clock offset = Clock.offset(current, Duration.ofMillis(1));
        System.out.println("Before sleep: " + offset.millis());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("After sleep: " + offset.millis());

    }

}
