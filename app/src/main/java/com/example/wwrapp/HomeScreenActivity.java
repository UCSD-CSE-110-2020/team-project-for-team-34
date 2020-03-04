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

import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;

import java.lang.ref.WeakReference;


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

    // Fitness service
    // TODO: Implement proper mocking of FitnessServices. Google Fit needs to be decoupled from
    // TODO: HomeScreenActivity
    public static IFitnessService fitnessService;
    // TODO: Eliminate this FitnessAsyncTask once proper dependency injection has been applied.
    private static FitnessAsyncTask fitnessRunner;

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

        // use this code to reset the last walk's stats
         SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
         SharedPreferences.Editor editor = spfs.edit();
         editor.clear();
         editor.apply();

         // Get fitness service, if one doesn't already exist
        if (fitnessService == null) {
            Intent intent = getIntent();
            String fitnessServiceKey = intent.getStringExtra(WWRConstants.EXTRA_FITNESS_SERVICE_TYPE_KEY);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

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

