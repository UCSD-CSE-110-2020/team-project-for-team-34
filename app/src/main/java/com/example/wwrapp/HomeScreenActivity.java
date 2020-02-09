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
    private static FitnessService fitnessService;

    // Numeric constants
    private static final int SLEEP_TIME = 1000;
    private static final double TENTHS_PLACE_ROUNDING_FACTOR = 10.0;

    // SharedPreferences
    public static final String STEPS_SHARED_PREF_NAME = "user_steps";
    public static final String TOTAL_STEPS_KEY = "totalSteps";

    private TextView mStepsView;
    private TextView mMilesView;

    private long mTotalSteps;
    private double mTotalMiles;

    private FitnessAsyncTask mFitnessRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mStepsView = findViewById(R.id.homeStepsTextView);
        mMilesView = findViewById(R.id.homeMilesTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for a saved height
        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this, HeightScreenActivity.class);
            startActivity(askHeight);
        }

        // Register the start walk button
        findViewById(R.id.startNewWalkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the updating of the home screen before starting the Walk
                if (!mFitnessRunner.isCancelled()) {
                    Log.w(TAG, "Fitness runner to be canceled");
                    mFitnessRunner.cancel(false);
                }
                Intent walk = new Intent(HomeScreenActivity.this,WalkActivity.class);
                startActivity(walk);
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
                Intent route = new Intent(HomeScreenActivity.this,RoutesActivity.class);
                startActivity(route);
            }
        });


        // Initialize the FitnessService implementation
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreenActivity homeScreenActivity) {
                return new GoogleFitAdapter(homeScreenActivity);
            }
        });
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();
        fitnessService.updateStepCount();
        // Start the Home screen steps/miles updating in the background
        mFitnessRunner = new FitnessAsyncTask();
        mFitnessRunner.execute();
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
                setMiles(mTotalSteps);
                displayStepsAndMiles();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mFitnessRunner.isCancelled()) {
            mFitnessRunner.cancel(false);
        }
    }


    private boolean checkHasHeight(){
        SharedPreferences saveHeight =
                getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
        int testVal = saveHeight.getInt(HeightScreenActivity.HEIGHT_FEET_KEY,-1);
        // If testVal == -1, then there was no height
        return testVal != - 1;
    }

    public void displayStepsAndMiles() {
        this.mStepsView.setText(String.valueOf(this.mTotalSteps));
        this.mMilesView.setText(String.valueOf(Math.round(this.mTotalMiles * TENTHS_PLACE_ROUNDING_FACTOR)/ TENTHS_PLACE_ROUNDING_FACTOR));
    }


    /**
     * Sets the miles based on the given stepCount
     * @param stepCount the number of steps in the day
     */
    public void setMiles(long stepCount){
        // Get the user's height
        SharedPreferences heightSharedPref =
                getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
        int feet = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_FEET_KEY, 0);
        int inches = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_INCHES_KEY, 0);
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

        // Get the user's height
        SharedPreferences heightSharedPref =
                getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
        int feet = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_FEET_KEY, 0);
        int inches = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_INCHES_KEY, 0);

        // Calculate the user's total miles from their steps and height
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        this.mTotalMiles = converter.getNumMiles(this.mTotalSteps);
        displayStepsAndMiles();
    }

    public static FitnessService getFitnessService() {
        return fitnessService;
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
            fitnessService.updateStepCount();
            // Update the miles based on the newly set step count
            setMiles(mTotalSteps);
            // Update the Home screen
            displayStepsAndMiles();
        }
    }
}

