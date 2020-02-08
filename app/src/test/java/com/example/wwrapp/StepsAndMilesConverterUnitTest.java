package com.example.wwrapp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for converting steps to miles
 */
public class StepsAndMilesConverterUnitTest {
    private StepsAndMilesConverter converter;

    @Before
    public void setup() {
        int heightFeet = 5;
        int heightInches = 3;
        converter = new StepsAndMilesConverter(heightFeet, heightInches);
    }

    @Test
    public void testGetNumMilesNormal() {
        long numSteps = 2435;
        double expectedMiles = 1;
        double acceptableError = 0.001;
        assertEquals(expectedMiles, converter.getNumMiles(numSteps), acceptableError);
    }

    @Test
    public void testGetNumMilesZeroSteps() {
        long numSteps = 0;
        double expectedMiles = 0;
        double acceptableError = 0.001;
        assertEquals(expectedMiles, converter.getNumMiles(numSteps), acceptableError);
    }
}
