package com.example.wwrapp.fitness;

import android.util.Log;

import com.example.wwrapp.utils.WWRConstants;

/**
 * Provides different Fitness Services for production, mocking, and UI testing.
 */
public abstract class FitnessServiceFactory {

    private static final String TAG = "FitnessServiceFactory";

    /**
     * A Factory method idiom for decoupling creation of the FitnessService from clients.
     * @param key the type of Fitness service to get
     * @return the Fitness service corresponding to the given key
     */
    public static IFitnessService createFitnessService(String key) {
        Log.d(TAG, "In createFitnessService() method");
        assert key != null;

        IFitnessService fitnessService = null;
        switch (key) {
            case WWRConstants.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY:
                fitnessService = FitnessApplication.getGoogleFitnessServiceInstance();
                break;
            case WWRConstants.DUMMY_FITNESS_SERVICE_FACTORY_KEY:
                fitnessService = FitnessApplication.getDummyFitnessServiceInstance();
                break;
        };
        return fitnessService;
    }
}
