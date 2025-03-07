package com.example.wwrapp.fitness;

import android.app.Activity;

/**
 * Defines operations that a walking fitness service must provide
 */
public interface IFitnessService {
    void setup();
    void startFitnessService(Activity activity);
    void stopFitnessService();
    void addSteps(long steps);
    void setTime(long milliseconds);
    long getTime();
    boolean isMockingTime();
}
