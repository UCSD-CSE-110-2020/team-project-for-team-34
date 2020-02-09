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

    public static final String HOURS_KEY = "HOURS_KEY";
    public static final String MINUTES_KEY = "MINUTES_KEY";
    public static final String SECONDS_KEY = "SECONDS_KEY";
    public static final String STEPS_KEY = "STEPS_KEY";
    public static final String MILES_KEY = "MILES_KEY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Log.d(TAG, "onCreate called");

        mStepsSharedPreference = getSharedPreferences(HomeScreenActivity.STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        mStartSteps = mStepsSharedPreference.getLong(HomeScreenActivity.TOTAL_STEPS_KEY,0);

        mHoursTextView = findViewById(R.id.hrs);
        mMinutesTextView = findViewById(R.id.mins);
        mSecondsTextView = findViewById(R.id.secs);
        mStepsView = findViewById(R.id.stepCount);
        mMilesView = findViewById(R.id.mileCount);
        mStopBtn = findViewById(R.id.stopButton);
        mWalkTimer = new TimerTask();
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
        Intent intent = new Intent(this, EnterWalkInformationActivity.class);
        intent.putExtra(HOURS_KEY, mHours);
        intent.putExtra(MINUTES_KEY, mMinutes);
        intent.putExtra(SECONDS_KEY, mSeconds);
        intent.putExtra(STEPS_KEY, mStepsTaken);
        intent.putExtra(MILES_KEY, mMiles);


        Log.d(TAG, "mHours is" + mHours);
        Log.d(TAG, "mMinutes is " + mMinutes);
        Log.d(TAG, "mSeconds is " + mSeconds);
        Log.d(TAG, "mStepsTaken is " + mStepsTaken);
        Log.d(TAG, "mMiles is " + mMiles);

        startActivity(intent);
        finish();
    }

    private class TimerTask extends AsyncTask<String,String, String> {
        private long time;
        private int feet;
        private int inches;

        @Override
        public void onPreExecute() {
            Log.d(TAG, "onPreExecute called");
            SharedPreferences heightSharedPref =
                    getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
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
            mHours = (int) (time / NUM_SECONDS_PER_HOUR);
            mMinutes = (int) ((time / NUM_SECONDS_PER_MINUTE) % NUM_SECONDS_PER_MINUTE);
            mSeconds = (int) (time % NUM_SECONDS_PER_MINUTE);
            Log.d(TAG, "time is " + time);
            mHoursTextView.setText(mHours + " hr");
            mMinutesTextView.setText(mMinutes + " min");
            mSecondsTextView.setText(mSeconds + " sec");
        }

        private void updateSteps() {
            HomeScreenActivity.getFitnessService().updateStepCount();
            mCurrSteps = mStepsSharedPreference.getLong(HomeScreenActivity.TOTAL_STEPS_KEY,0);
            mStepsTaken = mCurrSteps - mStartSteps;
            mStepsView.setText(Long.toString(mStepsTaken));

            // Calculate the user's total miles
            StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
            mMiles = converter.getNumMiles(mStepsTaken);
            //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
            mMiles = Math.round(mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
            mMilesView.setText("That's " + mMiles + " miles so far");
        }
    }


}
