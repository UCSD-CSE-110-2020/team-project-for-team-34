package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wwrapp.R;
import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.IUserFactory;
import com.example.wwrapp.models.TeamInvitation;
import com.example.wwrapp.services.DummyFitnessServiceWrapper;
import com.example.wwrapp.services.GoogleFitnessServiceWrapper;
import com.example.wwrapp.utils.StepsAndMilesConverter;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 * Home screen for the app
 */
public class HomeScreenActivity extends AppCompatActivity implements IFitnessObserver {
    private static final String TAG = "HomeScreenActivity";

    // Numeric constants
    private static final double TENTHS_PLACE_ROUNDING_FACTOR = 10.0;
    private static final int MOCK_ACTIVITY_REQUEST_CODE = 1;

    // String constants
    public static final String NO_LAST_WALK_TIME_TEXT = "No last walk time available";

    // True to enable the FitnessRunner, false otherwise
    private static boolean sEnableFitnessRunner = false;
    private static boolean sIgnoreHeight = true;

    public static boolean IS_MOCKING = false;

    // Views for data
    private TextView mStepsTextView;
    private TextView mMilesTextView;

    private TextView mLastWalkStepsTextView;
    private TextView mLastWalkMilesTextView;
    private TextView mLastWalkTimeTextView;

    // User data
    private int mFeet, mInches;
    private long mDailyTotalSteps;
    private double mDailyTotalMiles;
    private long mLastWalkSteps;
    private double mLastWalkMiles;
    private String mLastWalkTime;

    // Fitness service
    // TODO: Implement proper mocking of FitnessServices. Google Fit needs to be decoupled from
    // TODO: HomeScreenActivity
    // TODO: Eliminate this FitnessAsyncTask once proper dependency injection has been applied.

    public static GoogleSignInAccount account = null;
    private FirebaseFirestore mFirestore;

    private boolean tempUserExists;
    private boolean mUserIsBeingInvited;

    private IUser mUser;

    // TODO: Set this variable to true if you want to test the invite member screen
    // TODO: else set to false if you want to test the team screen
    public static boolean TESTING_USER_IS_BEING_INVITED;

    private String mFitnessServiceKey;
    private boolean mIsObserving;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.e(TAG, "In method onCreate");

        // Initialize the database
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Implement real sign-in logic. Using a dummy user for now to make testing possible.
        mUser = null;
        // Determine what type of user to use:
        String userType = getIntent().getStringExtra(WWRConstants.EXTRA_USER_TYPE_KEY);
        if (userType == null) {
            // Default to the mock user
            Log.d(TAG, "Creating Mock user");
            mUser = IUserFactory.createUser
                    (WWRConstants.MOCK_USER_FACTORY_KEY,
                            WWRConstants.MOCK_USER_NAME, WWRConstants.MOCK_USER_EMAIL
                    );
        } else {
            // TODO: Implement factory creation for Google user
            Log.d(TAG, "Creating Google user");
        }

        // TODO: Update this with actual sign-in logic later
        // Register the team screen button
        IUser finalUser = mUser;
        findViewById(R.id.teamScreenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Revisit this line once Google User has been fixed
//                DocumentReference docRef = db.collection(WWRConstants.USER_COLLECTION_KEY).document(account.getEmail());

                // TODO: Temporary fix for User
                // Check if the user is being invited by anyone:
                boolean userIsBeingInvited = userIsBeingInvited(finalUser.getEmail(), mFirestore);
                if (TESTING_USER_IS_BEING_INVITED) {
                    // If the user is being invited, prompt them to decide on the invites.
                    Intent intent = new Intent(HomeScreenActivity.this, InviteMemberScreenActivity.class);
                    intent.putExtra(WWRConstants.EXTRA_USER_KEY, finalUser);
                    startActivity(intent);
                } else {
                    // If the user is not being invited, take them to the Team screen.
                    Intent intent = new Intent(HomeScreenActivity.this, TeamActivity.class);
                    intent.putExtra(WWRConstants.EXTRA_USER_KEY, finalUser);
                    startActivity(intent);
                }
            }
        });

        // TODO: Implement signIn() once we know that Google Fit is working
        // signIn();

        // Check for a saved height. If there is not height, prompt the user to enter it.
        if (!sIgnoreHeight) { // <-- this evaluates to false when testing to skip the prompt
            if (!checkHasHeight()) {
                Intent askHeight = new Intent(HomeScreenActivity.this, HeightScreenActivity.class);
                startActivity(askHeight);
            }
        }

        // Determine what type of fitness service to use
        mFitnessServiceKey = getIntent().getStringExtra(WWRConstants.EXTRA_FITNESS_SERVICE_TYPE_KEY);
        startObservingFitnessService(mFitnessServiceKey);

        // Set up the main UI elements relating to walk stats
        mStepsTextView = findViewById(R.id.homeSteps);
        mMilesTextView = findViewById(R.id.homeMiles);
        mLastWalkStepsTextView = findViewById(R.id.lastWalkSteps);
        mLastWalkMilesTextView = findViewById(R.id.lastWalkDistance);
        mLastWalkTimeTextView = findViewById(R.id.lastWalkTime);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Register the start walk button
        findViewById(R.id.startNewWalkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalkActivity();
            }
        });

        // Register the routes screen button
        findViewById(R.id.routeScreenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoutesActivity();
            }
        });

        // Register the mock screen button
        findViewById(R.id.mockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMockActivity();
            }
        });

        // Save the values of stored height, steps and miles, and last walk stats to their
        // respective member variables
        initSavedData(getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE),
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE),
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE));

        // Update the UI
        updateHomeDisplay(mDailyTotalSteps, mDailyTotalMiles, mLastWalkSteps, mLastWalkMiles, mLastWalkTime);


        // use this code to reset the last walk's stats
        // TODO: Comment out in production. If you don't, the last walk stats won't work.
//        SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = spfs.edit();
//        editor.clear();
//        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In method onResume");
        Log.d(TAG, "Mocking? " + IS_MOCKING);

        // Re-register this activity as an observer if it has been un-registered.
        if (!mIsObserving) {
            // The current value of the key tells us which fitness service we last started.
            startObservingFitnessService(mFitnessServiceKey);
        }

        // TODO: Should these lines be in onResume or onCreate (or both) ?
        initSavedData(getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE),
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE),
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE));
        updateHomeDisplay(mDailyTotalSteps, mDailyTotalMiles, mLastWalkSteps, mLastWalkMiles, mLastWalkTime);
        Log.d(TAG, "onCreate daily steps = " + mDailyTotalSteps);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "In method onPause");

        // Un-register this activity from the fitness service if it was registered
        if (mIsObserving) {
            // The current value of the key tells us which fitness service we last started.
            stopObservingFitnessService(mFitnessServiceKey);
        }

        // Save whatever data the fitness service had provided up until now
        saveDataToSharedPreferences(getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE),
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE), mDailyTotalSteps, mLastWalkSteps, mLastWalkMiles, mLastWalkTime);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "In method onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In method onDestroy");
        Log.d(TAG, "mSteps jin nDestroy is = " + mDailyTotalSteps);
    }

    private void startObservingFitnessService(String fitnessServiceKey) {
        mIsObserving = true;
        // Provide a default implementation if key is null
        if (fitnessServiceKey == null) {
            // If the factory key is null, use the DummyFitnessService by default:
            IFitnessService dummyFS = FitnessServiceFactory.createFitnessService(WWRConstants.DEFAULT_FITNESS_SERVICE_FACTORY_KEY);

            // Down-cast the fitness service so we can add observers to it and start it.
            ((IFitnessSubject) dummyFS).registerObserver(this);
            ((DummyFitnessServiceWrapper) dummyFS).startDummyService();

            // Provide a value for the key so that we know in onResume() that we've already started
            // the service
            mFitnessServiceKey = WWRConstants.DEFAULT_FITNESS_SERVICE_FACTORY_KEY;
            return;
        }

        switch (fitnessServiceKey) {
            case WWRConstants.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY:
                IFitnessService googleFitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
                ((IFitnessSubject) googleFitnessService).registerObserver(this);
                ((GoogleFitnessServiceWrapper) googleFitnessService).startGoogleService(this);
                break;
            case WWRConstants.DUMMY_FITNESS_SERVICE_FACTORY_KEY:
                IFitnessService dummyFitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
                ((IFitnessSubject) dummyFitnessService).registerObserver(this);
                ((DummyFitnessServiceWrapper) dummyFitnessService).startDummyService();
                break;
            default:
                Log.w(TAG, "fitnessServiceKey not recognized: " + mFitnessServiceKey);
        }
    }

    private void stopObservingFitnessService(String fitnessServiceKey) {
        switch (fitnessServiceKey) {
            case WWRConstants.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY:
                Log.d(TAG, "Unregistering Google Fitness Service");
                IFitnessService googleFitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
                ((IFitnessSubject) googleFitnessService).removeObserver(this);
                break;
            case WWRConstants.DUMMY_FITNESS_SERVICE_FACTORY_KEY:
                Log.d(TAG, "Unregistering Dummy Fitness Service");
                IFitnessService dummyFitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
                ((IFitnessSubject) dummyFitnessService).removeObserver(this);
                break;
        }

        mIsObserving = false;
    }

    /**
     * Starts the WalkActivity
     */
    private void startWalkActivity() {
        Log.d(TAG, "In startWalkActivity()");
        Intent intent = new Intent(HomeScreenActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        startActivity(intent);
    }

    /**
     * Starts the RoutesActivity
     */
    private void startRoutesActivity() {
        Log.d(TAG, "In startRoutesActivity()");
        Intent intent = new Intent(HomeScreenActivity.this, RoutesActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY,mUser);
        startActivity(intent);
    }

    /**
     * Starts the mocking activity
     */
    private void startMockActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, MockWalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        startActivityForResult(intent, MOCK_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

        switch (requestCode) {
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            case 420:
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                return;
            case MOCK_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // TODO: Implement mock screen with the new Dummy Service
                    // Stop Google Fit
                    // Start the mocking service
                }
                break;

            // If authentication was required during google fit setup, this will be called after the user authenticates
            case WWRConstants.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "requestCode is from GoogleFit");
                    // TODO: Eliminate this code below if it's not really needed
                    // Update the steps/miles if returning from a walk
//                    setMiles(mDailyTotalSteps, mFeet, mInches);
//                    updateUi();
                } else {
                    Log.e(TAG, "ERROR, google fit result code: " + resultCode);
                }
                break;
        } // end switch
    }


    /**
     * Retrieves the user's stored height, daily steps and miles, and last walk stats and sets
     * the corresponding instance variables with those values.
     * @param heightSharedPreferences
     * @param lastWalkSharedPreferences
     * @param stepsSharedPreferences
     */
    private void initSavedData(SharedPreferences heightSharedPreferences,
                               SharedPreferences lastWalkSharedPreferences,
                               SharedPreferences stepsSharedPreferences) {
        // Get the user's height
        mFeet = heightSharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        mInches = heightSharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);

        // Get the user's steps, and use this value to calculate the miles
        mDailyTotalSteps = stepsSharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);
        StepsAndMilesConverter stepsAndMilesConverter = new StepsAndMilesConverter(mFeet, mInches);
        mDailyTotalMiles = stepsAndMilesConverter.getNumMiles(mDailyTotalSteps);

        // Get the last walk's stats
        mLastWalkSteps = lastWalkSharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
        mLastWalkMiles = lastWalkSharedPreferences.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
        mLastWalkTime = lastWalkSharedPreferences.getString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, HomeScreenActivity.NO_LAST_WALK_TIME_TEXT);
    }

    /**
     * Sets the values of the UI elements corresponding to the user's daily steps and miles
     * and last walk stats.
     * @param dailyTotalSteps
     * @param dailyTotalMiles
     * @param lastWalkSteps
     * @param lastWalkMiles
     * @param lastWalkTime
     */
    private void updateHomeDisplay(long dailyTotalSteps, double dailyTotalMiles,
                                   long lastWalkSteps, double lastWalkMiles, String lastWalkTime) {
        // Set values for steps and miles
        mStepsTextView.setText(String.valueOf(dailyTotalSteps));
        mMilesTextView.setText(String.valueOf(Math.round(dailyTotalMiles * TENTHS_PLACE_ROUNDING_FACTOR) /
                TENTHS_PLACE_ROUNDING_FACTOR));

        // Set values for last walk stats
        mLastWalkStepsTextView.setText(String.valueOf(lastWalkSteps));
        mLastWalkMilesTextView.setText(String.valueOf(lastWalkMiles));
        mLastWalkTimeTextView.setText(String.valueOf(lastWalkTime));
    }

    /**
     * Saves the user's daily steps and miles  and last walk stats to Shared Preferences.
     * @param stepsSharedPreference
     * @param lastWalkSharedPreference
     * @param dailyTotalSteps
     * @param lastWalkSteps
     * @param lastWalkMiles
     * @param lastWalkDate
     */
    private void saveDataToSharedPreferences(SharedPreferences stepsSharedPreference,
                                             SharedPreferences lastWalkSharedPreference,
                                             long dailyTotalSteps,
                                             long lastWalkSteps,
                                             double lastWalkMiles,
                                             String lastWalkDate) {
        // Save the daily steps
        Log.d(TAG, "Steps to save in Shared Preferences is " + dailyTotalSteps);
        SharedPreferences.Editor stepsEditor = stepsSharedPreference.edit();
        stepsEditor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, dailyTotalSteps);
        stepsEditor.apply();

        // Save the last walk
        SharedPreferences.Editor lastWalkEditor = lastWalkSharedPreference.edit();
        lastWalkEditor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, lastWalkSteps);
        lastWalkEditor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) lastWalkMiles);
        lastWalkEditor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, lastWalkDate);
        lastWalkEditor.apply();
    }

    /**
     * Sets the miles based on the given stepCount and height
     *
     * @param stepCount the number of steps in the day
     * @param feet      the user's height in feet
     * @param inches    the user's height in inches
     */
    public void setMiles(long stepCount, int feet, int inches) {
        // Calculate the user's total miles
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        this.mDailyTotalMiles = converter.getNumMiles(stepCount);
    }

    /**
     * Updates the total step count for the day
     *
     * @param stepCount the new step count
     */
    public void setStepCount(long stepCount) {
        mDailyTotalSteps = stepCount;
        // Set the miles based on the steps
        setMiles(mDailyTotalSteps, mFeet, mInches);
        // Update the UI
        updateHomeDisplay(mDailyTotalSteps, mDailyTotalMiles, mLastWalkSteps, mLastWalkMiles, mLastWalkTime);
    }

    public void setHeight(int feet, int inches) {
        mFeet = feet;
        mInches = inches;
    }

    @Override
    public void update(long steps) {
        Log.d(TAG, "In method update()");
        Log.d(TAG, "Steps is = " + steps);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDailyTotalSteps = steps;
                // Update the miles based on the newly set step count
                setMiles(mDailyTotalSteps, mFeet, mInches);
                // Update the Home screen
                updateHomeDisplay(HomeScreenActivity.this.mDailyTotalSteps,
                        HomeScreenActivity.this.mDailyTotalMiles,
                        HomeScreenActivity.this.mLastWalkSteps,
                        HomeScreenActivity.this.mLastWalkMiles,
                        HomeScreenActivity.this.mLastWalkTime);
            }
        });
    }


    // Flag methods for testing

    public static void setEnableFitnessRunner(boolean enableFitnessRunner) {
        HomeScreenActivity.sEnableFitnessRunner = enableFitnessRunner;
    }

    public static void setIgnoreHeight(boolean ignoreHeight) {
        HomeScreenActivity.sIgnoreHeight = ignoreHeight;
    }

    private void signIn() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            Log.d(TAG, "first time login");
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .addExtension(fitnessOptions)
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, options);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 420);
            boolean isEmailLoaded = false;
            while (!isEmailLoaded) {
                Log.d(TAG, "Email not loaded");
                isEmailLoaded = HomeScreenActivity.account.getEmail() != null;
            }
        } else {
            HomeScreenActivity.account = GoogleSignIn.getLastSignedInAccount(this);
            Log.d(TAG, "Email from last log in is " + account.getEmail());
        }
        createUser();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG, "handleSigninResult called");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "your email is " + account.getEmail(), Toast.LENGTH_SHORT).show();
            HomeScreenActivity.account = account;
            Log.d(TAG, "good");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private boolean userIsBeingInvited(String userEmail, FirebaseFirestore firestore) {
        // Get the "invitations" collection
        CollectionReference invitationsCol =
                mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);

        // Query for all invitations where the current user is the invitee:
        invitationsCol.whereEqualTo(TeamInvitation.FIELD_INVITEE, userEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mUserIsBeingInvited = (task.getResult().size() > 0);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return mUserIsBeingInvited;
    }

    public boolean userExists() {
        CollectionReference usersCollection = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);
        usersCollection
                .whereEqualTo(WWRConstants.USER_EMAIL_KEY, HomeScreenActivity.account.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            userExist();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        userDoesNotExist();
                    }
                });
        return tempUserExists;
    }

    public void createUser() {
        if (userExists()) {
            return;
        }
        boolean isEmailLoaded = false;
        while (!isEmailLoaded) {
            Log.d(TAG, "Email not loaded");
            isEmailLoaded = HomeScreenActivity.account.getDisplayName() != null;

        }
        Map<String, Object> user = new HashMap<>();
        user.put(WWRConstants.USER_EMAIL_KEY, HomeScreenActivity.account.getEmail());
        user.put(WWRConstants.USER_NAME_KEY, HomeScreenActivity.account.getDisplayName());
        user.put(WWRConstants.USER_ROUTES_OWNED_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        user.put(WWRConstants.USER_ROUTES_NOT_OWNED_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        user.put(WWRConstants.USER_TEAM_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(HomeScreenActivity.account.getEmail()).set(user);
    }

    private boolean checkHasHeight() {
        SharedPreferences saveHeight =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        int testVal = saveHeight.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, -1);
        // If testVal == -1, then there was no height
        return testVal != -1;
    }

    public void userExist() {
        tempUserExists = true;
    }

    public void userDoesNotExist() {
        tempUserExists = false;
    }


} // end class

