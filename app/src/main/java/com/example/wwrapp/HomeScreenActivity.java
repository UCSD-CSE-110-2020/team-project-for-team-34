package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.MockFitnessService;


/**
 * Home screen for the app
 */
public class HomeScreenActivity extends AppCompatActivity implements IFitnessObserver {
    private static final String TAG = "HomeScreenActivity";

    // Numeric constants
    private static final int SLEEP_TIME = 1000;
    private static final double TENTHS_PLACE_ROUNDING_FACTOR = 10.0;

    // String constants
    public static final String NO_LAST_WALK_TIME_TEXT = "No last walk time available";

    // FitnessService keys
    private static final String fitnessServiceKey = "GOOGLE_FIT";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    // True to enable the FitnessRunner, false otherwise
    private static boolean sEnableFitnessRunner = true;
    private static boolean sIgnoreHeight = false;


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

    private IFitnessService fitnessService;
    private static FitnessAsyncTask sFitnessService;


//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MockFitnessService.LocalBinder localService = (MockFitnessService.LocalBinder) service;
//            Log.d(TAG, "Assigned fitness service in onServiceConnected");
//            fitnessService = localService.getService();
//            IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
//            fitnessSubject.registerObserver(HomeScreenActivity.this);
//            mIsBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mIsBound = false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.d(TAG, "In method onCreate");

        mStepsTextView = findViewById(R.id.homeSteps);
        mMilesTextView = findViewById(R.id.homeMiles);
        mLastWalkStepsTextView = findViewById(R.id.lastWalkStepsTextView);
        mLastWalkMilesTextView = findViewById(R.id.lastWalkDistanceTextView);
        mLastWalkTimeTextView = findViewById(R.id.lastWalkTimeTextView);

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
                Intent route = new Intent(HomeScreenActivity.this, MockWalkActivity.class);
                route.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
                startActivity(route);
            }
        });

        // use this code to reset the last walk's stats
//        SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = spfs.edit();
//        editor.clear();
//        editor.apply();

        Log.d(TAG, "Right before creating Mock Fitness object");
        // fitnessService = MockFitnessApplication.getFitnessService();

//        sFitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
//        sFitnessService.setup();
//        sFitnessService.updateStepCount();
//
//        // Start the Home screen steps/miles updating in the background
//        mFitnessRunner = new FitnessAsyncTask();
//        if (sEnableFitnessRunner) {
//            mFitnessRunner.execute();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

//       If authentication was required during google fit setup, this will be called after the user authenticates
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(HomeScreenActivity.this, MockFitnessService.class);
        // Tell the service how many steps there are in the current day
//        intent.putExtra(WWRConstants.EXTRA_DAILY_STEPS_KEY, mDailyTotalSteps);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unbind from the fitness service
//        if (mIsBound) {
//            IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
//            fitnessSubject.removeObserver(HomeScreenActivity.this);
//            unbindService(serviceConnection);
//            mIsBound = false;
//        }
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

//        if (mIsBound) {
//            unbindService(serviceConnection);
//            mIsBound = false;
//        }

//        Intent intent = new Intent(HomeScreenActivity.this, MockFitnessService.class);
//        stopService(intent);
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In method onResume");
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
        mMilesTextView.setText(String.valueOf(Math.round(mDailyTotalMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR));
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
        Intent intent = new Intent(HomeScreenActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
        startActivity(intent);
    }

    /**
     * Starts the RoutesActivity
     */
    private void startRoutesActivity() {
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
    private class FitnessAsyncTask extends AsyncTask<String, String, String> {
        private String resp;

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
            // Ask the IFitnessService to update the step count, if applicable
            fitnessService.updateStepCount();
            // Update the miles based on the newly set step count
            setMiles(mDailyTotalSteps, mFeet, mInches);
            // Update the Home screen
            updateUi();
        }
    }
}

