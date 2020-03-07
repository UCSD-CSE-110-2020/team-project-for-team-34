package com.example.wwrapp.fitness;

import android.app.Application;

/**
 * A re-worked mock fitness application
 */
public class DummyFitnessApplication extends Application {
    private static IFitnessService sDummyFitnessService;
    private static IFitnessService sGoogleFitnessService;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the fitness service
        sDummyFitnessService = new DummyFitnessServiceWrapper(getApplicationContext());
    }

    public static IFitnessService getDummyFitnessServiceInstance() {
        return sDummyFitnessService;
    }
    public static IFitnessService getGoogleFitnessServiceInstance() {
        return sGoogleFitnessService;
    }

}
