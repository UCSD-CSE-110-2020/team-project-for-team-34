package com.example.wwrapp.utils;

/**
 * Constants for Firestore and Firebase
 */
public class FirestoreConstants {
    public static final String PATH_SLASH_SEPARATOR = "/";
    public static final String DOT_STR = ".";

    // "users" branch
    public static final String FIRESTORE_COLLECTION_USERS_PATH = "users";
    public static final String FIRESTORE_COLLECTION_MY_ROUTES_PATH = "myRoutes"; // users/someUser/myRoutes
    public static final String FIRESTORE_COLLECTION_MY_INVITEES_PATH = "myInvitees"; // users/someUser/myInvitees

    // "teams" branch
    public static final String FIRESTORE_COLLECTION_TEAMS_PATH = "teams";
    public static final String FIRESTORE_DOCUMENT_TEAM_PATH = "team"; // teams/team
    public static final String FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH = "teamMembers"; // teams/team/teamMembers
    public static final String FIRESTORE_COLLECTION_TEAM_ROUTES_PATH = "teamRoutes"; // teams/team/teamRoutes

    public static final String FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH = "walkers"; // teams/team/teamRoutes/someRoute/walkers

    // Firestore user invite status constants

    public static final String FIRESTORE_TEAM_INVITE_ACCEPTED = "accepted";
    public static final String FIRESTORE_TEAM_INVITE_PENDING = "pending";

    public static final String FIRESTORE_DEFAULT_TEAM_NAME = "";
    public static final String FIRESTORE_DEFAULT_TEAM_STATUS = "";
    public static final String FIRESTORE_DEFAULT_INVITER_NAME = "";
    public static final String FIRESTORE_DEFAULT_INVITER_EMAIL = "";
    public static final String FIRESTORE_DEFAULT_WALK_STATUS = "";


    // Attributes of a mock user
    public static final String MOCK_USER_NAME = "DD";
    public static final String MOCK_USER_EMAIL = "DD@";

    public static final String WWR_USER_NAME = "WWR Name";
    public static final String WWR_USER_EMAIL = "WWR Email";


    public static final String SECOND_MOCK_USER_NAME = "secondMockName";
    public static final String SECOND_MOCK_USER_EMAIL = "secondMockEmail";

    public static final String THIRD_MOCK_USER_NAME = "thirdMockName";
    public static final String THIRD_MOCK_USER_EMAIL = "thirdMockEmail";

//    public static final String FIRESTORE_DOCUMENT_ROUTE_NAME = "routeName";
//    public static final String FIRESTORE_DOCUMENT_STARTING_POINT = "startingPoint";
//    public static String USER_TEAM_PENDING_KEY = "teamPendingDemoTeam";
//    public static String TEAM_NAME_KEY = "name";
//    public static String TEAM_USERS_KEY = "users";
//    public static String ROUTE_STARTING_POINT_KEY = "startingPoint";
//    public static String ROUTE_STEPS_KEY = "steps";
//    public static String ROUTE_MILES_KEY = "miles";
//    public static String ROUTE_DATE_KEY = "date";
//    public static String ROUTE_TAGS_KEY = "tag";
//    public static String TAG_NAME_KEY = "name";
//    public static String TAG_LOOP_KEY = "loop";
//    public static String TAG_OUTANDBACK_KEY = "outAndBack";
//    public static String TAG_FLAT_KEY = "flat";
//    public static String TAG_HILLY_KEY = "hilly";
//    public static String TAG_STREETS_KEY = "streets";
//    public static String TAG_TRAIL_KEY = "trail";
//    public static String TAG_EVEN_KEY = "even";
//    public static String TAG_UNEVEN_KEY = "uneven";
//    public static String TAG_EASY_KEY = "easy";
//    public static String TAG_MODERATE_KEY = "moderate";
//    public static String TAG_DIFFICULT_KEY = "difficult";
//    public static String TAG_FAVORITE_KEY = "favorite";

}
