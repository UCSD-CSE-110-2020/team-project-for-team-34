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

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;

public class WalkActivity extends AppCompatActivity {

    private static final String TAG = "WalkActivity";

    private TextView mHoursTextView, mMinutesTextView, mSecondsTextView, mStepsView, mMilesView;
    private Button mStopBtn;
    private TimerTask mWalkTimer;

    private long mStartSteps, mCurrSteps, mStepsTaken;
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
        setContentView(R.layout.activity_walk);
        Log.d(TAG, "onCreate called");

        mDateTime = LocalDateTime.now();
        mStepsSharedPreference = getSharedPreferences(HomeScreenActivity.STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        mStartSteps = mStepsSharedPreference.getLong(HomeScreenActivity.TOTAL_STEPS_KEY,0);

        mHoursTextView = findViewById(R.id.hrs);
        mMinutesTextView = findViewById(R.id.mins);
        mSecondsTextView = findViewById(R.id.secs);
        mStepsView = findViewById(R.id.stepCount);
        mMilesView = findViewById(R.id.mileCount);
        mStopBtn = findViewById(R.id.stopButton);
        mWalkTimer = new TimerTask(this);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWalkTimer.cancel(false);
                HomeScreenActivity.getFitnessService().updateStepCount();
                launchWalkInformationActivity();
                finish();
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
    public void launchWalkInformationActivity() {
        // Pass the Walk data onto the next Activity
        Intent intent = new Intent(this, EnterWalkInformationActivity.class);
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);
        Walk walk = new Walk(mStepsTaken, mMiles, mDateTime, duration);
        intent.putExtra(WALK_KEY, walk);

        Log.d(TAG, "mHours is" + mHours);
        Log.d(TAG, "mMinutes is " + mMinutes);
        Log.d(TAG, "mSeconds is " + mSeconds);
        Log.d(TAG, "mStepsTaken is " + mStepsTaken);
        Log.d(TAG, "mMiles is " + mMiles);

        startActivity(intent);
        finish();
    }

    private static class TimerTask extends AsyncTask<String,String, String> {
        private long time;
        private int feet;
        private int inches;

        private WeakReference<WalkActivity> walkActivityWeakReference;

        public TimerTask(WalkActivity context) {
            walkActivityWeakReference = new WeakReference<>(context);
        }

        @Override
        public void onPreExecute() {
            Log.d(TAG, "onPreExecute called");
            // Get a reference to the activity
            WalkActivity walkActivity = walkActivityWeakReference.get();
            SharedPreferences heightSharedPref = walkActivity.getSharedPreferences
                    (HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
            feet = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_FEET_KEY, 0);
            inches = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_INCHES_KEY, 0);
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
            updateSteps();
        }

        private void updateTime() {
            Log.d(TAG, "updateTime called");
            WalkActivity walkActivity = walkActivityWeakReference.get();
            walkActivity.mHours = (int) (time / NUM_SECONDS_PER_HOUR);
            walkActivity.mMinutes = (int) ((time / NUM_SECONDS_PER_MINUTE) % NUM_SECONDS_PER_MINUTE);
            walkActivity.mSeconds = (int) (time % NUM_SECONDS_PER_MINUTE);
            Log.d(TAG, "time is " + time);
            walkActivity.mHoursTextView.setText(walkActivity.mHours + " hr");
            walkActivity.mMinutesTextView.setText(walkActivity.mMinutes + " min");
            walkActivity.mSecondsTextView.setText(walkActivity.mSeconds + " sec");
        }

        private void updateSteps() {
            WalkActivity walkActivity = walkActivityWeakReference.get();

            HomeScreenActivity.getFitnessService().updateStepCount();
            walkActivity.mCurrSteps = walkActivity.mStepsSharedPreference.getLong(HomeScreenActivity.TOTAL_STEPS_KEY,0);
            walkActivity.mStepsTaken = walkActivity.mCurrSteps - walkActivity.mStartSteps;
            walkActivity.mStepsView.setText(Long.toString(walkActivity.mStepsTaken));

            // Calculate the user's total miles
            StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
            walkActivity.mMiles = converter.getNumMiles(walkActivity.mStepsTaken);
            //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
            walkActivity.mMiles = Math.round(walkActivity.mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
            walkActivity.mMilesView.setText("That's " + walkActivity.mMiles + " miles so far");
        }
    }


}
