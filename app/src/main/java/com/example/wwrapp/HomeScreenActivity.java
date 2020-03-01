package com.example.wwrapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.GoogleFitAdapter;
import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.fitness.MockFitnessService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

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

    // FitnessService keys
    private static final String fitnessServiceKey = "GOOGLE_FIT";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    // True to enable the FitnessRunner, false otherwise
    private static boolean sEnableFitnessRunner = true;
    private static boolean sIgnoreHeight = false;

    public static boolean IS_MOCKING = false;
    private boolean mIsBound;

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

    public static IFitnessService fitnessService;
    private static FitnessAsyncTask fitnessRunner;

    private ServiceConnection googleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MockFitnessService.LocalBinder localService = (MockFitnessService.LocalBinder) service;
            Log.d(TAG, "Assigned fitness service in onServiceConnected");
            fitnessService = localService.getService();
            IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
            fitnessSubject.registerObserver(HomeScreenActivity.this);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };


    private ServiceConnection mockServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MockFitnessService.LocalBinder localService = (MockFitnessService.LocalBinder) service;
            Log.d(TAG, "Assigned fitness service in onServiceConnected");
            fitnessService = localService.getService();
            IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
            fitnessSubject.registerObserver(HomeScreenActivity.this);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.e(TAG, "In method onCreate");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> test = new HashMap<>();
        test.put(WWRConstants.FIRESTORE_DOCUMENT_ROUTE_NAME, "testRouteName");
        firestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document("TestUser")
                .set(test).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

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

        // use this code to reset the last walk's stats
         SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
         SharedPreferences.Editor editor = spfs.edit();
         editor.clear();
         editor.apply();

        Log.d(TAG, "Right before creating Mock Fitness object");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

        // If mocking is requested
        if (requestCode == MOCK_ACTIVITY_REQUEST_CODE) {
            if (IS_MOCKING) {
                // Stop Google Fit
                fitnessRunner.cancel(false);
                // Start the mocking service
                Intent intent = new Intent(HomeScreenActivity.this, MockFitnessService.class);
                // Tell the service how many steps there are in the current day
                intent.putExtra(WWRConstants.EXTRA_DAILY_STEPS_KEY, mDailyTotalSteps);
                bindService(intent, mockServiceConnection, Context.BIND_AUTO_CREATE);
                startService(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (IS_MOCKING) {
            // Cancel Google Fit
            if (!fitnessRunner.isCancelled()) {
                fitnessRunner.cancel(false);
            }
            Intent intent = new Intent(HomeScreenActivity.this, MockFitnessService.class);
            // Tell the service how many steps there are in the current day
            intent.putExtra(WWRConstants.EXTRA_DAILY_STEPS_KEY, mDailyTotalSteps);
            bindService(intent, mockServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!fitnessRunner.isCancelled()) {
            fitnessRunner.cancel(false);
        }
        if (IS_MOCKING) {
            // Unbind from the fitness service
            if (mIsBound) {
                IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
                fitnessSubject.removeObserver(HomeScreenActivity.this);
                unbindService(mockServiceConnection);
                mIsBound = false;
            }
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
            if (mIsBound) {
                unbindService(mockServiceConnection);
                mIsBound = false;
            }
            Intent intent = new Intent(HomeScreenActivity.this, MockFitnessService.class);
            stopService(intent);
        }
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In method onResume");
        Log.d(TAG , "Mocking? " + IS_MOCKING);
        // If not mocking, use Google Fit
        if (!IS_MOCKING) {
            // Initialize the FitnessService implementation
            FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
                @Override
                public IFitnessService create(HomeScreenActivity homeScreenActivity) {
                    return new GoogleFitAdapter(homeScreenActivity);
                }
            });
            fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
            fitnessService.setup();
            fitnessService.updateStepCount();

            // Start the Home screen steps/miles updating in the background
            fitnessRunner = new FitnessAsyncTask(this);
            if (sEnableFitnessRunner) {
                fitnessRunner.execute();
            }
        }
        initSavedData();
        updateUi();
    }

    private boolean checkHasHeight() {
        SharedPreferences saveHeight =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        int testVal = saveHeight.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, -1);
        // If testVal == -1, then there was no height
        return testVal != -1;
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
        if (!fitnessRunner.isCancelled()) {
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
        if (!fitnessRunner.isCancelled()) {
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

