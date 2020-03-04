package com.example.wwrapp.fitness;

import android.util.Log;

/**
 * A Dummy Fitness Service that does nothing. Use this for testing UI screens to avoid the
 * Google Fitness Service sign-in.
 */
public class DummyFitnessService implements IFitnessService {
    private static final String TAG = "DummyFitnessService";
    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {
        Log.d(TAG, "In method setup()");
    }

    @Override
    public void updateStepCount() {
        Log.d(TAG, "In method updateStepCount");
    }
}
