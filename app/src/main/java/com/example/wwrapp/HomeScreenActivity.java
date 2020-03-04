package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.IUserFactory;
import com.example.wwrapp.model.TeamInvitation;
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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Home screen for the app
 */
public class HomeScreenActivity extends AppCompatActivity implements IFitnessObserver {
    private static final String TAG = "HomeScreenActivity";

    // Numeric constants
    private static final int SLEEP_TIME = 1000;
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
    public static IFitnessService fitnessService;
    // TODO: Eliminate this FitnessAsyncTask once proper dependency injection has been applied.
    private static FitnessAsyncTask fitnessRunner;

    public static GoogleSignInAccount account = null;
    private FirebaseFirestore mFirestore;

    private boolean tempUserExists;
    private boolean mUserIsBeingInvited;

    // TODO: Set this variable to true if you want to test the invite member screen
    // TODO: else set to false if you want to test the team screen
    public static boolean TESTING_USER_IS_BEING_INVITED;

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG, "handleSigninResult called");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "your email is " + account.getEmail(), Toast.LENGTH_SHORT).show();
            HomeScreenActivity.account = account;
            Log.d(TAG,"good");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.e(TAG, "In method onCreate");

        mStepsTextView = findViewById(R.id.homeSteps);
        mMilesTextView = findViewById(R.id.homeMiles);
        mLastWalkStepsTextView = findViewById(R.id.lastWalkSteps);
        mLastWalkMilesTextView = findViewById(R.id.lastWalkDistance);
        mLastWalkTimeTextView = findViewById(R.id.lastWalkTime);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for a saved height
        if (!sIgnoreHeight) {
            if (!checkHasHeight()) {
                Intent askHeight = new Intent(HomeScreenActivity.this, HeightScreenActivity.class);
                startActivity(askHeight);
            }
        }

        // Set up stored inches, steps, and miles
        initSavedData();

        // Update the UI
        updateUi();

        // Register the start walk button
        findViewById(R.id.startNewWalkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Walk
                startWalkActivity();
            }
        });

        // Register the routes screen button
        findViewById(R.id.routeScreenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Routes screen
                startRoutesActivity();
            }
        });

        // Register the mock screen button
        findViewById(R.id.mockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, MockWalkActivity.class);
                intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
                startActivityForResult(intent, MOCK_ACTIVITY_REQUEST_CODE);
            }
        });

        // TODO: Implement real sign-in logic. Using a dummy user for now to make testing possible.
        IUser user = null;
        // Determine what type of user to use:
        String userType = getIntent().getStringExtra(WWRConstants.EXTRA_USER_TYPE_KEY);
        if (userType == null) {
            // Default to the mock user
            Log.d(TAG, "Creating Mock user");
            user = IUserFactory.createUser
                    (WWRConstants.MOCK_USER_FACTORY_KEY,
                            WWRConstants.MOCK_USER_NAME, WWRConstants. MOCK_USER_EMAIL,
                            WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);
        } else {
            // TODO: Implement factory creation for Google user
            Log.d(TAG, "Creating Google user");
        }

        // TODO: Update this with actual sign-in logic later
        // Register the team screen button
        IUser finalUser = user;
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

        // use this code to reset the last walk's stats
        // TODO: Comment out in production.
         SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
         SharedPreferences.Editor editor = spfs.edit();
         editor.clear();
         editor.apply();

        // Get fitness service, if one doesn't already exist
        if (fitnessService == null) {
            String fitnessServiceKey = getIntent().getStringExtra(WWRConstants.EXTRA_FITNESS_SERVICE_TYPE_KEY);
            // If the factory key is null, use the DummyFitnessService by default:
            if (fitnessServiceKey == null) {
                fitnessService = FitnessServiceFactory.createFitnessService(WWRConstants.DUMMY_FITNESS_SERVICE_FACTORY_KEY, this);
            } else {
                fitnessService = FitnessServiceFactory.createFitnessService(fitnessServiceKey, this);
            }

            fitnessService.setup();
            fitnessService.updateStepCount();

            // TODO: Remove this coupling of the fitness AsyncTask
            // Check if Google Fit is being used; only start this async task if Google Fit is used
            if (WWRConstants.GOOGLE_FIT_FITNESS_SERVICE_FACTORY_KEY.equals(fitnessServiceKey)) {
                // Start the Home screen steps/miles updating in the background
                fitnessRunner = new FitnessAsyncTask(this);
                if (sEnableFitnessRunner) {
                    fitnessRunner.execute();
                }
            }
        }


         //Initialize DB
         mFirestore = FirebaseFirestore.getInstance();
        // TODO: Implement signIn() once we know that Google Fit is working
         // signIn();
    }

    private void signIn() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
        if(GoogleSignIn.getLastSignedInAccount(this) == null) {
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
        }
        else {
            HomeScreenActivity.account = GoogleSignIn.getLastSignedInAccount(this);
            Log.d(TAG, "Email from last log in is " + account.getEmail());
        }
        createUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 420) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return;
        }

        // If mocking is requested
        // If returning from the mock screen
        if (requestCode == MOCK_ACTIVITY_REQUEST_CODE) {
            if (IS_MOCKING) {
                // Stop Google Fit
                fitnessRunner.cancel(false);
                // Start the mocking service
                // TODO: Implement mocking service
            }
        } else {
            // If authentication was required during google fit setup, this will be called after the user authenticates
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == fitnessService.getRequestCode()) {
                    Log.d(TAG, "requestCode is from GoogleFit");
                    // Update the steps/miles if returning from a walk
                    fitnessService.updateStepCount();
                    setMiles(mDailyTotalSteps, mFeet, mInches);
                    updateUi();
                }
            } else {
                Log.e(TAG, "ERROR, google fit result code: " + resultCode);
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        if (IS_MOCKING) {
            // Cancel Google Fit
            if (fitnessRunner != null && !fitnessRunner.isCancelled()) {
                fitnessRunner.cancel(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fitnessRunner != null && !fitnessRunner.isCancelled()) {
            fitnessRunner.cancel(false);
        }
        if (IS_MOCKING) {
            // TODO: Implement true mock
        }
        saveData();
        Log.d(TAG, "In method onPause");
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

        if (IS_MOCKING) {
            // TODO: Implement true mock
        }
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In method onResume");
        Log.d(TAG , "Mocking? " + IS_MOCKING);

        // TODO: Keep in mind that you may have to reinitialize the fitness service in onResume

        initSavedData();
        updateUi();
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
        if(userExists()) { return; }
        boolean isEmailLoaded = false;
        while(!isEmailLoaded) {
            Log.d(TAG, "Email not loaded");
            isEmailLoaded = HomeScreenActivity.account.getDisplayName() != null;

        }
        Map<String, Object> user = new HashMap<>();
        user.put(WWRConstants.USER_EMAIL_KEY, HomeScreenActivity.account.getEmail());
        user.put(WWRConstants.USER_NAME_KEY , HomeScreenActivity.account.getDisplayName());
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


    public void initSavedData() {
        // Get the user's height
        SharedPreferences heightSharedPref =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        mFeet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        mInches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);

        // Get the user's steps
        SharedPreferences stepsSharedPref = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
        mDailyTotalSteps = stepsSharedPref.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);

        StepsAndMilesConverter stepsAndMilesConverter = new StepsAndMilesConverter(mFeet, mInches);
        mDailyTotalMiles = stepsAndMilesConverter.getNumMiles(mDailyTotalSteps);

        // Get the last walk's stats
        SharedPreferences sharedPreferences =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        mLastWalkSteps = sharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
        mLastWalkMiles = sharedPreferences.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
        mLastWalkTime = sharedPreferences.getString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, HomeScreenActivity.NO_LAST_WALK_TIME_TEXT);
    }

    public void saveData() {
        // Save the daily steps
        SharedPreferences stepsSharedPreference =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor stepsEditor = stepsSharedPreference.edit();
        stepsEditor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, mDailyTotalSteps);
        stepsEditor.apply();

        // Save the last walk
        SharedPreferences lastWalkSharedPreference =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor lastWalkEditor = lastWalkSharedPreference.edit();
        lastWalkEditor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, mLastWalkSteps);
        lastWalkEditor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) mLastWalkMiles);
        lastWalkEditor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, mLastWalkTime);
        lastWalkEditor.apply();
    }

    public void updateUi() {
        mStepsTextView.setText(String.valueOf(mDailyTotalSteps));
        mMilesTextView.setText(String.valueOf(Math.round(mDailyTotalMiles * TENTHS_PLACE_ROUNDING_FACTOR) /
                TENTHS_PLACE_ROUNDING_FACTOR));

        mLastWalkStepsTextView.setText(String.valueOf(mLastWalkSteps));
        mLastWalkMilesTextView.setText(String.valueOf(mLastWalkMiles));
        mLastWalkTimeTextView.setText(String.valueOf(mLastWalkTime));
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
        updateUi();
    }

    public void setHeight(int feet, int inches) {
        mFeet = feet;
        mInches = inches;
    }


    // Flag methods for testing

    public static void setEnableFitnessRunner(boolean enableFitnessRunner) {
        HomeScreenActivity.sEnableFitnessRunner = enableFitnessRunner;
    }

    public static void setIgnoreHeight(boolean ignoreHeight) {
        HomeScreenActivity.sIgnoreHeight = ignoreHeight;
    }

    /**
     * Starts the WalkActivity
     */
    private void startWalkActivity() {
        if (fitnessRunner != null && !fitnessRunner.isCancelled()) {
            Log.w(TAG, "Fitness runner to be canceled");
            fitnessRunner.cancel(false);
        }
        Intent intent = new Intent(HomeScreenActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        startActivity(intent);
    }

    /**
     * Starts the RoutesActivity
     */
    private void startRoutesActivity() {
        if (fitnessRunner != null && !fitnessRunner.isCancelled()) {
            fitnessRunner.cancel(false);
        }
        Intent intent = new Intent(HomeScreenActivity.this, RoutesActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        startActivity(intent);
    }

    @Override
    public void update(long steps) {
        Log.d(TAG, "In method update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDailyTotalSteps = steps;
                // Update the miles based on the newly set step count
                setMiles(mDailyTotalSteps, mFeet, mInches);
                // Update the Home screen
                updateUi();
            }
        });
    }

    /**
     * Updates the steps/miles display on the Home screen
     */
    private static class FitnessAsyncTask extends AsyncTask<String, String, String> {
        private String resp;
        private WeakReference<HomeScreenActivity> homeScreenActivityWeakReference;

        public FitnessAsyncTask(HomeScreenActivity context) {
            homeScreenActivityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
                // Check for updates to the step count
                publishProgress();
            }
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... update) {
            HomeScreenActivity homeScreenActivity = homeScreenActivityWeakReference.get();
            // Ask the IFitnessService to update the step count, if applicable
            homeScreenActivity.fitnessService.updateStepCount();
            // Update the miles based on the newly set step count
            homeScreenActivity.setMiles(homeScreenActivity.mDailyTotalSteps,
                    homeScreenActivity.mFeet, homeScreenActivity.mInches);
            // Update the Home screen
            homeScreenActivity.updateUi();
        }
    }
}

