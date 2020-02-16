package com.example.wwrapp;

/**
 * Contains constants for use throughout the project
 */
public final class WWRConstants {

    /**
     * Prevent external instantiation
     */
    private WWRConstants() {
    }

    // Intent extra keys
    // Prefix each key with the namespace to avoid collisions

    // Caller ids
    public static final String EXTRA_CALLER_ID_KEY = "com.example.wwrapp.EXTRA_CALLER_ID";
    public static final String EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID =
            "com.example.wwrapp.EXTRA_HOME_SCREEN_ACTIVITY";
    public static final String EXTRA_WALK_ACTIVITY_CALLER_ID =
            "com.example.wwrapp.EXTRA_WALK_ACTIVITY";
    public static final String EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID =
            "com.example.wwrapp.EXTRA_ENTER_WALK_INFORMATION";
    public static final String EXTRA_ROUTE_DETAIL_ACTIVITY_CALLER_ID =
            "com.example.wwrapp.EXTRA_ROUTE_DETAIL_ACTIVITY";

    // Keys
    public static final String EXTRA_WALK_OBJECT_KEY = "com.example.wwrapp.EXTRA_WALK_KEY";
    public static final String EXTRA_ROUTE_OBJECT_KEY = "com.example.wwrapp.EXTRA_ROUTE_OBJECT_KEY";
    public static final String EXTRA_DAILY_STEPS_KEY = "com.example.wwrapp.EXTRA_DAILY_STEPS_KEY";

    // SharedPreferences constants

    // Height file
    public static final String SHARED_PREFERENCES_HEIGHT_FILE_NAME = "com.example.wwrapp.SHARED_PREFERENCES_HEIGHT_FILE_NAME";
    public static final String SHARED_PREFERENCES_HEIGHT_FEET_KEY = "com.example.wwrapp.SHARED_PREFERENCES_HEIGHT_FEET_KEY";
    public static final String SHARED_PREFERENCES_HEIGHT_INCHES_KEY = "com.example.wwrapp.SHARED_PREFERENCES_HEIGHT_INCHES_KEY";

    // Steps file
    public static final String SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME =
            "com.example.wwrapp.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME";
    public static final String SHARED_PREFERENCES_TOTAL_STEPS_KEY =
            "com.example.wwrapp.SHARED_PREFERENCES_TOTAL_STEPS_KEY";

    // Last walk file
    public static final String SHARED_PREFERENCES_LAST_WALK_FILE_NAME = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_FILE_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_STEPS_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_STEPS_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_MILES_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_MILES_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_DATE_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_DATE_NAME";

    // Date format
    public static final String DATE_FORMATTER_PATTERN = "MM-dd-yyyy: HH:mm";


    // Mock Walk was called
    public static final String EXTRA_FITNESS_SERVICE_VERSION_KEY = "com.example.wwrapp.EXTRA_FITNESS_SERVICE_VERSION_KEY";
    public static String GOOGLE_FIT_VERSION = "com.example.wwrapp.GOOGLE_FIT_VERSION";
    public static String MOCK_FITNESS_SERVICE_VERSION = "com.example.wwrapp.MOCK_FITNESS_SERVICE_VERSION";
    public static String FITNESS_SERVICE_VERSION = GOOGLE_FIT_VERSION;


    public static final boolean PRODUCTION_VERSION = false;
}
