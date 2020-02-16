package com.example.wwrapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.database.Walk;
import com.example.wwrapp.fitness.IFitnessObserver;

import java.time.LocalDateTime;

public class MockWalkActivity extends AppCompatActivity implements IFitnessObserver {

    private static final String TAG = "MockWalkActivity";

    private TextView mHoursTextView, mMinutesTextView, mSecondsTextView, mStepsView, mMilesView;
    private Button mStopBtn, mAddStepsBtn;
    private TimerTask mWalkTimer;

    private long mSteps = 0;
    private double mMiles;

    private SharedPreferences mStepsSharedPreference;

    // Numeric constants
    private static final int SLEEP_TIME = 1000;
    private static final double TENTHS_PLACE_ROUNDING_FACTOR = 10.0;

    // Time numeric constants
    private static final int NUM_SECONDS_PER_HOUR = 3600;
    private static final int NUM_MINUTES_PER_HOUR = 60;
    private static final int NUM_SECONDS_PER_MINUTE = 60;

    private int mHours;
    private int mMinutes;
    private int mSeconds;
    private int feet;
    private int inches;

    private LocalDateTime mDateTime;

    // Intent keys
    public static final String HOURS_KEY = "HOURS_KEY";
    public static final String MINUTES_KEY = "MINUTES_KEY";
    public static final String SECONDS_KEY = "SECONDS_KEY";
    public static final String STEPS_KEY = "STEPS_KEY";
    public static final String MILES_KEY = "MILES_KEY";
    public static final String WALK_KEY = "WALK_KEY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_walk);
        Log.d(TAG, "onCreate called");

        mDateTime = LocalDateTime.now();
        mStepsSharedPreference = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences heightSharedPref =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        feet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        inches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        mHoursTextView = findViewById(R.id.mock_hrs);
        mMinutesTextView = findViewById(R.id.mock_mins);
        mSecondsTextView = findViewById(R.id.mock_secs);
        mStepsView = findViewById(R.id.mock_stepCount);
        mMilesView = findViewById(R.id.mock_mileCount);
        mStopBtn = findViewById(R.id.mock_stopButton);
        mAddStepsBtn = findViewById(R.id.mock_addStepsButton);
        mWalkTimer = new TimerTask();
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop Timer
                mWalkTimer.cancel(false);
                saveData();
                launchHomeScreenActivity();
                finish();
            }
        });

        mAddStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSteps += 500;
                saveData();
                updateViews();
            }
        });
        mWalkTimer.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }
    }

    /**
     * Launches the activity to enter walk information
     */
    public void launchHomeScreenActivity() {
        // Pass the Walk data onto the next Activity
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);
        Walk walk = new Walk(mSteps, mMiles, mDateTime, duration);
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.putExtra(WWRConstants.EXTRA_FITNESS_SERVICE_VERSION_KEY, WWRConstants.MOCK_FITNESS_SERVICE_VERSION);
        Log.d(TAG, "mHours is" + mHours);
        Log.d(TAG, "mMinutes is " + mMinutes);
        Log.d(TAG, "mSeconds is " + mSeconds);
        Log.d(TAG, "mSteps is " + mSteps);
        Log.d(TAG, "mMiles is " + mMiles);
        startActivity(intent);
        finish();
    }

    public void saveData() {
        //Update total steps
        long oldSteps = mStepsSharedPreference.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY,0);
        long currStpes = oldSteps + mSteps;
        SharedPreferences.Editor editor = mStepsSharedPreference.edit();
        editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, currStpes);
        editor.apply();

        //Update most recent walk
        SharedPreferences spfs = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor spfsEditor = spfs.edit();
        long lastSteps = spfs.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
        long currLastSteps = lastSteps + mSteps;
        spfsEditor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, currLastSteps);
        float lastMiles = spfs.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
        float currLastMiles = lastMiles + ((float)mMiles);
        spfsEditor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, currLastMiles);
        spfsEditor.apply();
        mSteps = 0;
    }

    @Override
    public void update(long steps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSteps = steps;
                saveData();
                updateViews();
            }
        });
    }

    private void updateViews() {
        mStepsView.setText(Long.toString(mSteps));
        Log.d(TAG, "Feet: " + feet);
        Log.d(TAG, "Inches: " + inches);
        // Calculate the user's total miles
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        mMiles = converter.getNumMiles(mSteps);
        //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
        mMiles = Math.round(mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
        mMilesView.setText("That's " + mMiles + " miles so far");
    }

    private class TimerTask extends AsyncTask<String,String, String> {
        private long time;

        @Override
        public void onPreExecute() {
            Log.d(TAG, "onPreExecute called");
            SharedPreferences heightSharedPref =
                    getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
            feet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
            inches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);
            Log.d(TAG, "Feet: " + feet);
            Log.d(TAG, "Inches: " + inches);
        }

        @Override
        protected String doInBackground(String ... params) {
            Log.d(TAG, "doInBackground called");
            while (!isCancelled()) {
                try {
                    Thread.sleep(SLEEP_TIME);
                    ++time;
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
            return null;
        }

        @Override
        public void onProgressUpdate(String ... text) {
            Log.d(TAG, "onProgressUpdate called");
            updateTime();
            // implemented from IFitnessObserver
            // updateSteps();
        }

        private void updateTime() {
            Log.d(TAG, "updateTime called");
            mHours = (int) (time / NUM_SECONDS_PER_HOUR);
            mMinutes = (int) ((time / NUM_SECONDS_PER_MINUTE) % NUM_SECONDS_PER_MINUTE);
            mSeconds = (int) (time % NUM_SECONDS_PER_MINUTE);
            Log.d(TAG, "time is " + time);
            mHoursTextView.setText(mHours + " hr");
            mMinutesTextView.setText(mMinutes + " min");
            mSecondsTextView.setText(mSeconds + " sec");
        }
    }
}
