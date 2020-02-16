package com.example.wwrapp.fitness;

import android.app.Application;

/**
 * Container class for mock fitness services to avoid pitfalls of singleton pattern
 * Credits go to Piazza post 403
 */
public class MockFitnessApplication extends Application {
    private static IFitnessService mockFitnessService;

    @Override
    public void onCreate() {
        super.onCreate();
        mockFitnessService = new MockFitnessService();
    }

    public static IFitnessService getFitnessService() {
        return mockFitnessService;
    }
}

