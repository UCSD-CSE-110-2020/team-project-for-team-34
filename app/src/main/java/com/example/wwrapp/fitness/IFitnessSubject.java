package com.example.wwrapp.fitness;

/**
 * Fitness subject for the observer pattern
 */
public interface IFitnessSubject {
    void registerObserver(IFitnessObserver fitnessObserver);
    void removeObserver(IFitnessObserver fitnessObserver);
    void notifyObservers();
}
