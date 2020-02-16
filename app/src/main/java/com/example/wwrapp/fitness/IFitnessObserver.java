package com.example.wwrapp.fitness;

/**
 * Fitness observer for the observer pattern
 */
public interface IFitnessObserver {
    /**
     * Updates the step count for the day
     * @param steps the daily step count
     */
    void update(long steps);
}
