package com.example.wwrapp.fitness;

import android.util.Log;

import com.example.wwrapp.HomeScreenActivity;
import com.example.wwrapp.WWRConstants;

import java.util.HashMap;
import java.util.Map;




/**
 * Provides different Fitness Services for production, mocking, and UI testing.
 */
public class FitnessServiceFactory {

    private static final String TAG = "FitnessServiceFactory";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static IFitnessService create(String key, HomeScreenActivity homeScreenActivity) {
        Log.i(TAG, String.format("creating IFitnessService with key %s", key));
        return blueprints.get(key).create(homeScreenActivity);
    }

    public interface BluePrint {
        IFitnessService create(HomeScreenActivity homeScreenActivity);
    }

    /**
     * A Factory method idiom for decoupling creation of the FitnessService from clients.
     * @param key
     * @param activity
     * @return
     */
    public static IFitnessService createFitnessService(String key, HomeScreenActivity activity) {
        Log.d(TAG, "In createFitnessService() method");
        IFitnessService fitnessService = null;
        switch (key) {
            case WWRConstants.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY:
                fitnessService = new GoogleFitAdapter(activity);
                break;
            case WWRConstants.DUMMY_FITNESS_SERVICE_FACTORY_KEY:
                fitnessService = new DummyFitnessService();
                break;
        };

        return fitnessService;
    }


}
