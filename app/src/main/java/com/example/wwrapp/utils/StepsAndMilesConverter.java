package com.example.wwrapp.utils;

/**
 * Class to convert between steps and miles using height.
 */
public class StepsAndMilesConverter {
    private int feet;
    private int inches;

    // Constants for converting between steps and miles
    private static final int NUM_INCHES_PER_FOOT = 12;
    private static final double STRIDE_LENGTH_FACTOR = 0.413;
    private static final int NUM_FEET_PER_MILE = 5280;

    public StepsAndMilesConverter(int feet, int inches) {
        this.feet = feet;
        this.inches = inches;
    }

    /**
     * Returns the number of miles walked based on the given number of steps and provided height.
     * The formula is calculated as (height in inches) * (stride length factor) * (number of steps)
     * divided by the (number of feet in a mile)
     * Uses the calculation in Option 2 in the following link:
     * https://www.openfit.com/how-many-steps-walk-per-mile
     * @param numSteps the number of steps walked
     * @return the number of miles walked based on the given number of steps and provided height.
     */
    public double getNumMiles(long numSteps) {
        int height = this.feet * NUM_INCHES_PER_FOOT + this.inches;
        double strideLength = (height * STRIDE_LENGTH_FACTOR) / NUM_INCHES_PER_FOOT;
        double feetWalked = strideLength * numSteps;
        // Convert feet to miles
        return feetWalked / NUM_FEET_PER_MILE;
    }

    public void setFeetAndInches(int feet, int inches) {
        this.feet = feet;
        this.inches = inches;
    }
}
