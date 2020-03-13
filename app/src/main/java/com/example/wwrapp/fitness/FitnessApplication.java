package com.example.wwrapp.fitness;

import android.app.Application;

import com.example.wwrapp.services.DummyFitnessServiceWrapper;
import com.example.wwrapp.services.GoogleFitnessServiceWrapper;

/**
 * Provides global state for Fitness Services
 */
public class FitnessApplication extends Application {
    private static IFitnessService sCurrentFitnessService;
    private static IFitnessService sDummyFitnessService;
    private static IFitnessService sGoogleFitnessService;


    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the fitness services, but don't start them.
        sDummyFitnessService = new DummyFitnessServiceWrapper(getApplicationContext());
        sGoogleFitnessService = new GoogleFitnessServiceWrapper(getApplicationContext());
    }

    public static IFitnessService getDummyFitnessServiceInstance() {
        return sDummyFitnessService;
    }

    public static IFitnessService getGoogleFitnessServiceInstance() {
        return sGoogleFitnessService;
    }
}
