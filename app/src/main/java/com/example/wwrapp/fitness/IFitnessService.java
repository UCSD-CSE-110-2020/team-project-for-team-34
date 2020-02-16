package com.example.wwrapp.fitness;

/**
 * Defines operations that a walking fitness service must provide
 */
public interface IFitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount();
}
