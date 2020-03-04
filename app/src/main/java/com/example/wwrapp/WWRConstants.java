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
    public static final String EXTRA_ROUTES_ACTIVITY_CALLER_ID = "com.example.wwrapp.EXTRA_ROUTE_ACTIVITY";

    // Keys
    public static final String EXTRA_WALK_OBJECT_KEY = "com.example.wwrapp.EXTRA_WALK_KEY";
    public static final String EXTRA_ROUTE_OBJECT_KEY = "com.example.wwrapp.EXTRA_ROUTE_OBJECT_KEY";
    public static final String EXTRA_MANUALLY_CREATED_ROUTE_KEY =
            "com.example.wwrapp.EXTRA_MANUALLY_CREATED_ROUTE_KEY";
    public static final String EXTRA_DAILY_STEPS_KEY = "com.example.wwrapp.EXTRA_DAILY_STEPS_KEY";

    // For user objects
    public static final String EXTRA_USER_KEY = "com.example.wwrapp.EXTRA_USER_KEY";
    public static final String EXTRA_USER_TYPE_KEY = "com.example.wwrapp.EXTRA_USER_TYPE_KEY";



    // Firestore Keys
    public static final String EXTRA_ROUTE_ID_KEY = "com.example.wwrapp.EXTRA_ROUTE_ID_KEY";
    public static final String EXTRA_ROUTE_PATH_KEY = "com.example.wwrapp.EXTRA_ROUTE_PATH_KEY";


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

    // Time file
    public static final String SHARED_PREFERENCES_SYSTEM_TIME_FILE_NAME =
            "com.example.wwrapp.SHARED_PREFERENCES_SYSTEM_TIME_FILE_NAME";
    public static final String SHARED_PREFERENCES_SYSYTEM_TIME_KEY =
            "com.example.wwrapp.SHARED_PREFERENCES_SYSTEM_TIME_KEY";

    // Last walk file
    public static final String SHARED_PREFERENCES_LAST_WALK_FILE_NAME = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_FILE_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_STEPS_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_STEPS_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_MILES_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_MILES_NAME";
    public static final String SHARED_PREFERENCES_LAST_WALK_DATE_KEY = "com.example.wwrapp.SHARED_PREFERENCES_LAST_WALK_DATE_NAME";


    // Date format
    public static final String DATE_FORMATTER_PATTERN_DETAILED = "MM-dd-yyyy: HH:mm";
    public static final String DATE_FORMATTER_PATTERN_SUMMARY = "MM-dd-yyyy";



    // Fitness Service constants
    public static final String EXTRA_FITNESS_SERVICE_TYPE_KEY = "com.example.wwrapp.EXTRA_FITNESS_SERVICE_VERSION_KEY";
    public static final String GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY =
            "com.example.wwrapp.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY";
    public static final String DUMMY_FITNESS_SERVICE_FACTORY_KEY =
            "com.example.wwrapp.DUMMY_FITNESS_SERVICE_FACTORY_KEY";

    // User Factory keys/types
    public static final String GOOGLE_USER_FACTORY_KEY = "com.example.wwrapp.GOOGLE_USER_FACTORY_KEY";
    public static final String MOCK_USER_FACTORY_KEY = "com.example.wwrapp.MOCK_USER_FACTORY_KEY";


    public static final String GOOGLE_FIT_VERSION = "com.example.wwrapp.GOOGLE_FIT_VERSION";
    // Default fitness service
    public static String FITNESS_SERVICE_VERSION = GOOGLE_FIT_VERSION;
    // Mocks
    public static final String MOCK_FITNESS_SERVICE_VERSION = "com.example.wwrapp.MOCK_FITNESS_SERVICE_VERSION";
    public static final long NO_MOCK_TIME = -1;

    // Firestore collection constants
    public static final String FIRESTORE_COLLECTION_USER_PATH = "users";
    public static final String FIRESTORE_COLLECTION_MY_ROUTES_PATH = "myRoutes";

    public static final String FIRESTORE_COLLECTION_TEAMS_PATH = "teams";
    public static final String FIRESTORE_COLLECTION_INVITATIONS_PATH = "invitations";



    // For testing only
    public static final String FIRESTORE_DOCUMENT_DUMMY_USER_PATH = "dummyUser";


    // Firestore document constants
    public static final String FIRESTORE_DOCUMENT_ROUTE_NAME = "routeName";
    public static final String FIRESTORE_DOCUMENT_STARTING_POINT = "startingPoint";

    // Firestore user invite status constants
    public static final String FIRESTORE_TEAM_INVITE_ACCEPTED = "accepted";
    public static final String FIRESTORE_TEAM_INVITE_DECLINED = "declined";
    public static final String FIRESTORE_TEAM_INVITE_PENDING = "pending";

    public static final boolean PRODUCTION_VERSION = false;
}
