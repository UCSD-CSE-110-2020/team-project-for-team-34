package com.example.wwrapp.utils;

/**
 * Constants for Firestore and Firebase
 */
public class FirestoreConstants {
    // "users" branch
    public static final String FIRESTORE_COLLECTION_USERS_PATH = "users";
    public static final String FIRESTORE_COLLECTION_MY_ROUTES_PATH = "myRoutes"; // users/someUser/myRoutes
    public static final String FIRESTORE_COLLECTION_MY_INVITEES_PATH = "myInvitees"; // users/someUser/myInvitees

    public static final String FIRESTORE_COLLECTION_INVITATIONS_PATH = "invitations";


    // "teams" branch
    public static final String FIRESTORE_COLLECTION_TEAMS_PATH = "teams";
    public static final String FIRESTORE_DOCUMENT_TEAM_PATH = "team"; // teams/team
    public static final String FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH = "teamMembers"; // teams/team/teamMembers

    public static final String FIRESTORE_COLLECTION_TEAMMATE_ROUTES_PATH = "teamRoutes"; // teams/team/teamRoutes


    public static final String FIRESTORE_DOCUMENT_ROUTE_NAME = "routeName";
    public static final String FIRESTORE_DOCUMENT_STARTING_POINT = "startingPoint";

    // Firestore user invite status constants
    public static final String FIRESTORE_TEAM_INVITE_ACCEPTED = "accepted";
    public static final String FIRESTORE_TEAM_INVITE_DECLINED = "declined";
    public static final String FIRESTORE_TEAM_INVITE_PENDING = "pending";

    public static final String FIRESTORE_DOCUMENT_TEAM_NAME = "wwrTeam";

    // Attributes of a mock user
    public static final String MOCK_USER_NAME = "NOT A NAME";
    public static final String MOCK_USER_EMAIL = "NOT AN EMAIL";

    public static final String SECOND_MOCK_USER_NAME = "secondMockName";
    public static final String SECOND_MOCK_USER_EMAIL = "secondMockEmail";

    public static final String THIRD_MOCK_USER_NAME = "thirdMockName";
    public static final String THIRD_MOCK_USER_EMAIL = "thirdMockEmail";


    //DataBase collection Strings
    public static String TEAMS_COLLECTION_KEY = "teams";
    public static String USERS_COLLECITON_KEY = "users";
    public static String USER_TEAM_PENDING_KEY = "teamPendingDemoTeam";
    public static String TEAM_NAME_KEY = "name";
    public static String TEAM_USERS_KEY = "users";
    public static String ROUTE_STARTING_POINT_KEY = "startingPoint";
    public static String ROUTE_STEPS_KEY = "steps";
    public static String ROUTE_MILES_KEY = "miles";
    public static String ROUTE_DATE_KEY = "date";
    public static String ROUTE_TAGS_KEY = "tag";
    public static String TAG_NAME_KEY = "name";
    public static String TAG_LOOP_KEY = "loop";
    public static String TAG_OUTANDBACK_KEY = "outAndBack";
    public static String TAG_FLAT_KEY = "flat";
    public static String TAG_HILLY_KEY = "hilly";
    public static String TAG_STREETS_KEY = "streets";
    public static String TAG_TRAIL_KEY = "trail";
    public static String TAG_EVEN_KEY = "even";
    public static String TAG_UNEVEN_KEY = "uneven";
    public static String TAG_EASY_KEY = "easy";
    public static String TAG_MODERATE_KEY = "moderate";
    public static String TAG_DIFFICULT_KEY = "difficult";
    public static String TAG_FAVORITE_KEY = "favorite";


    // For testing only
    public static final String FIRESTORE_DOCUMENT_DUMMY_USER_PATH = "dummyUser";
}
