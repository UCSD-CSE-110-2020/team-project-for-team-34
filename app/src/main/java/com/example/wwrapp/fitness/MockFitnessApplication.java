package com.example.wwrapp.fitness;

import android.app.Application;
import android.content.Context;

/**
 * Container class for mock fitness services to avoid pitfalls of singleton pattern
 * Credits go to Piazza post 403
 */
public class MockFitnessApplication extends Application {
    private static IFitnessService mockFitnessService;
    private static Context sApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mockFitnessService = new MockFitnessService();
        sApplicationContext = getApplicationContext();
    }

    public static IFitnessService getFitnessService() {
        return mockFitnessService;
    }

    public static Context getAppContext() {
        return sApplicationContext;
    }
}

