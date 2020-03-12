package com.example.wwrapp;

import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class DateComparisonUnitTest {

    /**
     * Returns true if the two date objects are on the same calendar day, false otherwise.
     * @param before
     * @param after
     * @return
     */
    public static boolean areSameDay(LocalDateTime before, LocalDateTime after) {
        int dayDiff = Math.abs(after.getDayOfYear() - before.getDayOfYear());
        return dayDiff > 0;
    }

    @Test
    public void testDaysAreOneDayApart() {
        LocalDateTime before = LocalDateTime.of(2020, 1, 5, 20, 30, 30);
        LocalDateTime after = LocalDateTime.of(2020, 1, 6, 0, 30, 30);
        assertTrue(areSameDay(before, after));
    }

    @Test
    public void testDaysAreOneDayApartYearsDifferent() {
        LocalDateTime before = LocalDateTime.of(2020, 1, 5, 20, 30, 30);
        LocalDateTime after = LocalDateTime.of(2019, 1, 4, 6, 0, 30, 30);
        assertTrue(areSameDay(before, after));
    }

    @Test
    public void testDaysAreMoreThanOneDayApart() {
        LocalDateTime before = LocalDateTime.of(2020, 1, 5, 20, 30, 30);
        LocalDateTime after = LocalDateTime.of(2020, 1, 10, 6, 0, 30, 30);
        assertTrue(areSameDay(before, after));
    }

    @Test
    public void testDaysAreNotOneDayApart() {
        LocalDateTime before = LocalDateTime.of(2020, 1, 5, 20, 30, 30);
        LocalDateTime after = LocalDateTime.of(2020, 1, 5, 0, 30, 30);
        assertFalse(areSameDay(before, after));
    }
}
