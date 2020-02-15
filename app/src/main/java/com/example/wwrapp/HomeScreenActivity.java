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

import com.example.wwrapp.fitness.FitnessService;
import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.GoogleFitAdapter;


/**
 * Home screen for the app
 */
public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreenActivity";
    private static final String fitnessServiceKey = "GOOGLE_FIT";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    // True to enable the FitnessRunner, false otherwise
    private static boolean sEnableFitnessRunner = true;
    private static boolean sIgnoreHeight = false;

    public static final String NO_LAST_WALK_TIME_TEXT = "No last walk time available";

    private static FitnessService sFitnessService;

    // Numeric constants
    private static final int SLEEP_TIME = 1000;
    private static final double TENTHS_PLACE_ROUNDING_FACTOR = 10.0;

    private TextView mStepsView;
    private TextView mMilesView;

    private long mTotalSteps;
    private double mTotalMiles;

    // User's height
    private int mFeet, mInches;

    private FitnessAsyncTask mFitnessRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mStepsView = findViewById(R.id.homeSteps);
        mMilesView = findViewById(R.id.homeMiles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Check for a saved height
        if(!sIgnoreHeight){
        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this, HeightScreenActivity.class);
            startActivity(askHeight);
        }}

        // Get the user's height
        SharedPreferences heightSharedPref =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        mFeet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        mInches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);

        // Register the start walk button
        findViewById(R.id.startNewWalkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Walk
                if (!mFitnessRunner.isCancelled()) {
                    Log.w(TAG, "Fitness runner to be canceled");
                    mFitnessRunner.cancel(false);
                }
                startWalkActivity();
            }
        });

        // Register the routes screen button
        findViewById(R.id.routeScreenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Routes screen
                if (!mFitnessRunner.isCancelled()) {
                    mFitnessRunner.cancel(false);
                }
                startRoutesActivity();
            }
        });

        // Register the mock screen button
        findViewById(R.id.mockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Routes screen
                if (!mFitnessRunner.isCancelled()) {
                    mFitnessRunner.cancel(false);
                }
                Intent route = new Intent(HomeScreenActivity.this, MockWalkActivity.class);
                route.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID);
                startActivity(route);
            }
        });

        // Update the last walk display, if applicable
        TextView lastWalkSteps = findViewById(R.id.lastWalkSteps);
        TextView lastWalkMiles = findViewById(R.id.lastWalkDistance);
        TextView lastWalkTime = findViewById(R.id.lastWalkTime);

        // use this code to reset the last walk's stats
        SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = spfs.edit();
        editor.clear();
        editor.apply();

        SharedPreferences sharedPreferences =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        long lastSteps = sharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
        float lastMiles = sharedPreferences.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
        String lastTime = sharedPreferences.getString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, HomeScreenActivity.NO_LAST_WALK_TIME_TEXT);

        lastWalkSteps.setText(String.valueOf(lastSteps));
        lastWalkMiles.setText(String.valueOf(lastMiles));
        lastWalkTime.setText(lastTime);

        // Initialize the FitnessService implementation
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreenActivity homeScreenActivity) {
                return new GoogleFitAdapter(homeScreenActivity);
            }
        });
        sFitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        sFitnessService.setup();
        sFitnessService.updateStepCount();

        // Start the Home screen steps/miles updating in the background
        mFitnessRunner = new FitnessAsyncTask();
        if (sEnableFitnessRunner) {
            mFitnessRunner.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == sFitnessService.getRequestCode()) {
                Log.d(TAG, "requestCode is from GoogleFit");
                // Update the steps/miles if returning from a walk
                sFitnessService.updateStepCount();
                setMiles(mTotalSteps, mFeet, mInches);
                displayStepsAndMiles();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "In method onPause");
        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "In method onStop");

        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In method onDestroy");

        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In method onResume");

        TextView lastWalkSteps = findViewById(R.id.lastWalkSteps);
        TextView lastWalkMiles = findViewById(R.id.lastWalkDistance);
        TextView lastWalkTime = findViewById(R.id.lastWalkTime);
        SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        long lastSteps = spfs.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
        float lastMiles = spfs.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
        String lastTime = spfs.getString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, HomeScreenActivity.NO_LAST_WALK_TIME_TEXT);
        lastWalkSteps.setText(String.valueOf(lastSteps));
        lastWalkMiles.setText(String.valueOf(lastMiles));
        lastWalkTime.setText(lastTime);
    }


    private boolean checkHasHeight(){
        SharedPreferences saveHeight =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        int testVal = saveHeight.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY,-1);
        // If testVal == -1, then there was no height
        return testVal != - 1;
    }

    public void displayStepsAndMiles() {
        this.mStepsView.setText(String.valueOf(this.mTotalSteps));
        this.mMilesView.setText(String.valueOf(Math.round(this.mTotalMiles * TENTHS_PLACE_ROUNDING_FACTOR)/ TENTHS_PLACE_ROUNDING_FACTOR));
    }


    /**
     * Sets the miles based on the given stepCount and height
     * @param stepCount the number of steps in the day
     * @param feet the user's height in feet
     * @param inches the user's height in inches
     */
    public void setMiles(long stepCount, int feet, int inches){
        // Calculate the user's total miles
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        this.mTotalMiles = converter.getNumMiles(stepCount);
    }

    /**
     * Updates the total step count for the day
     * @param stepCount the new step count
     */
    public void setStepCount(long stepCount) {
        mTotalSteps = stepCount;
        // Set the miles based on the steps
        setMiles(mTotalSteps, mFeet, mInches);
        // Update the UI
        displayStepsAndMiles();
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

    public static FitnessService getFitnessService() {
        return sFitnessService;
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

    /**
     * Updates the steps/miles display on the Home screen
     */
    private class FitnessAsyncTask extends AsyncTask<String, String, String> {
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            while(!isCancelled()) {
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
            // Ask the FitnessService to update the step count, if applicable
            sFitnessService.updateStepCount();
            // Update the miles based on the newly set step count
            setMiles(mTotalSteps, mFeet, mInches);
            // Update the Home screen
            displayStepsAndMiles();
        }
    }
}

