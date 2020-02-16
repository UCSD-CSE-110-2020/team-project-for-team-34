package com.example.wwrapp.fitness;

import android.util.Log;

import com.example.wwrapp.HomeScreenActivity;

import java.util.HashMap;
import java.util.Map;

public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

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
}
