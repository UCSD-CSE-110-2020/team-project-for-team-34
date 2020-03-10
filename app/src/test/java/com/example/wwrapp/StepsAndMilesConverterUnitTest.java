package com.example.wwrapp;

import com.example.wwrapp.utils.StepsAndMilesConverter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for converting steps to miles
 */
public class StepsAndMilesConverterUnitTest {
    private StepsAndMilesConverter converter;
    private double acceptableError = 0.001;


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
        assertEquals(expectedMiles, converter.getNumMiles(numSteps), acceptableError);
    }

    @Test
    public void testGetNumMilesZeroSteps() {
        long numSteps = 0;
        double expectedMiles = 0;
        double acceptableError = 0.001;
        assertEquals(expectedMiles, converter.getNumMiles(numSteps), acceptableError);
    }

    @Test
    public void testEdgeInput(){
        // when feet & inch = 0
        int testFeet = 0;
        int testInches = 0;
        StepsAndMilesConverter tester = new StepsAndMilesConverter(testFeet, testInches);
        int testSteps = 10000;
        float expectedMiles = 0;
        assertEquals(expectedMiles, tester.getNumMiles(testSteps), acceptableError);


        // when step = 0 and test setter in StepsAndMilesConverter
        testFeet = 5;
        testInches = 11;
        tester.setFeetAndInches(testFeet, testInches);
        testSteps = 0;
        expectedMiles = 0;
        assertEquals(expectedMiles, tester.getNumMiles(testSteps), acceptableError);
    }

    @Test
    public void testNormalInput(){
        // when step and feet and inch are valid
        int testFeet = 5;
        int testInches = 11;
        StepsAndMilesConverter tester = new StepsAndMilesConverter(testFeet, testInches);
        int testSteps = 10000;
        float expectedMiles = (float)(Math.round(4.627998737 * 10)/ 10.0);
        float testMiles = (float)(Math.round(tester.getNumMiles(testSteps) * 10)/ 10.0);
        assertEquals(expectedMiles, testMiles, acceptableError);

    }
}
