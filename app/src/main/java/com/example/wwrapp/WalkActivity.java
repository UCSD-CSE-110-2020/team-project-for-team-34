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
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.fitness.MockFitnessService;
import com.example.wwrapp.model.Route;
import com.example.wwrapp.model.Walk;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Represents a walking session.
 */
public class WalkActivity extends AppCompatActivity implements IFitnessObserver {

    private static final String TAG = "WalkActivity";

    // adding a boolean for testing
    private static boolean ignoreTimer = false;

    private TextView mHoursTextView, mMinutesTextView, mSecondsTextView, mStepsView, mMilesView;
    private TextView mTitleTextView;
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
    private IFitnessService fitnessService;
    private boolean mIsBound;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MockFitnessService.LocalBinder localService = (MockFitnessService.LocalBinder) service;
            fitnessService = localService.getService();
            IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
            fitnessSubject.registerObserver(WalkActivity.this);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);


        mDateTime = LocalDateTime.now();
        mStepsSharedPreference = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
        mStartSteps = mStepsSharedPreference.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);

        mHoursTextView = findViewById(R.id.hrs);
        mMinutesTextView = findViewById(R.id.mins);
        mSecondsTextView = findViewById(R.id.secs);
        mStepsView = findViewById(R.id.stepCount);
        mMilesView = findViewById(R.id.mileCount);
        mStopBtn = findViewById(R.id.stopButton);

        mTitleTextView = findViewById(R.id.walk_screen_title);
        // Check if there's a title to set
        Intent receivedIntent = getIntent();

        Route route = (Route) (receivedIntent.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
        Log.d(TAG, "Route object is " + route);
        // If there's a route extra
        if (route != null) {
            mTitleTextView.setText(route.getRouteName());
        }

        if(ignoreTimer){
            handleWalkStopped();
        }

        if (HomeScreenActivity.IS_MOCKING) {
            Intent intent = new Intent(WalkActivity.this, MockFitnessService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }

        mWalkTimer = new TimerTask(this);
        // Register the "stop walk" button

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Stop button pressed");
                mWalkTimer.cancel(false);
                // HomeScreenActivity.getFitnessService().updateStepCount();
                handleWalkStopped();
            }
        });
        mWalkTimer.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }

        if (HomeScreenActivity.IS_MOCKING) {
            // Unbind from the fitness service
            if (mIsBound) {
                IFitnessSubject fitnessSubject = (IFitnessSubject) fitnessService;
                fitnessSubject.removeObserver(WalkActivity.this);
                unbindService(serviceConnection);
                mIsBound = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }
        if (HomeScreenActivity.IS_MOCKING) {
            Intent intent = new Intent(WalkActivity.this, MockFitnessService.class);
            stopService(intent);
        }
    }

    /**
     * Launches the activity to enter walk information
     */
    private void startEnterWalkInformationActivity() {
        Log.d(TAG, "startEnterWalkInformationActivity called");
        // Pass the Walk data onto the next Activity
        Intent intent = new Intent(this, EnterWalkInformationActivity.class);
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);

        // Convert LocalDateTime to Date
        Date date = Date.from(mDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Walk walk = new Walk(mStepsTaken, mMiles, date, duration);
        intent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_WALK_ACTIVITY_CALLER_ID);

        Log.d(TAG, walk.toString());

        startActivity(intent);
    }

    /**
     * Returns data to the RoutesDetailActivity
     */
    private void returnToRouteDetailActivity() {
        Log.d(TAG, "returnToRouteDetail called");
        Intent returnIntent = new Intent();
        // Store the new Walk as an extra
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);

        // Create a new Walk
        // TODO: Test that a walk is updated with a dummy number of steps.
        // TODO: Remove the dummy steps in production.
        mStepsTaken = 100;
        // Convert LocalDateTime to Date
        Date date = Date.from(mDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Walk walk = new Walk(mStepsTaken, mMiles, date, duration);

        Log.d(TAG, "Walk object returned to RouteDetail is\n" + walk.toString());
        returnIntent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        // Pass this Intent back
        setResult(Activity.RESULT_OK, returnIntent);
    }

    /**
     * Decides what to do when the walk is stopped
     */
    private void handleWalkStopped() {
        Log.d(TAG, "handleWalkStopped called");
        // Check which activity started this current one:
        Intent incomingIntent = getIntent();
        String callerId = incomingIntent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
        // If the Home screen started this activity
        if (callerId.equals(WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID)) {
            startEnterWalkInformationActivity();
        } else if (callerId.equals(WWRConstants.EXTRA_ROUTE_DETAIL_ACTIVITY_CALLER_ID)) {
            returnToRouteDetailActivity();
        } else {
            Log.d(TAG, "The activity that started WalkActivity is not meant to start it");
        }

        // Close up this activity
        finish();
    }

    @Override
    public void update(long steps) {
        Log.d(TAG, "IFitness Implementation: update called");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrSteps = steps;
                updateSteps();
            }
        });

    }

    public void updateSteps() {
        Log.d(TAG, "updateSteps called");
        mStepsTaken = mCurrSteps - mStartSteps;

        Log.d(TAG, "Start steps is: " + mStartSteps);
        Log.d(TAG, "Current steps is: " + mCurrSteps);
        Log.d(TAG, "Steps taken is: " + mStepsTaken);

        mStepsView.setText(Long.toString(mStepsTaken));

        // Calculate the user's total miles
        SharedPreferences heightSharedPref = getSharedPreferences
                (WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
        int feet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
        int inches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        mMiles = converter.getNumMiles(mStepsTaken);
        //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
        mMiles = Math.round(mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
        mMilesView.setText("That's " + mMiles + " miles so far");


    }

    private static class TimerTask extends AsyncTask<String, String, String> {
        private long time;
        private int feet;
        private int inches;

        private WeakReference<WalkActivity> walkActivityWeakReference;

        public TimerTask(WalkActivity context) {
            walkActivityWeakReference = new WeakReference<>(context);
        }

        @Override
        public void onPreExecute() {
            Log.d(TAG, "TimerTask: onPreExecute called");
            // Get a reference to the activity
            WalkActivity walkActivity = walkActivityWeakReference.get();
            SharedPreferences heightSharedPref = walkActivity.getSharedPreferences
                    (WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
            feet = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, 0);
            inches = heightSharedPref.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, 0);
            Log.d(TAG, "Feet: " + feet);
            Log.d(TAG, "Inches: " + inches);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "TimerTask: doInBackground called");
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
        public void onProgressUpdate(String... text) {
            Log.d(TAG, "TimerTask: onProgressUpdate called");
            updateTime();
            // Call updateSteps in the update() via FitnessObserver
            //If not mocking, the Timer Task updates the steps
            if (!HomeScreenActivity.IS_MOCKING) {
                updateSteps();
            }
        }

        private void updateTime() {
            Log.d(TAG, "TimerTask: updateTime called");
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
            HomeScreenActivity.fitnessService.updateStepCount();
            walkActivity.mCurrSteps = walkActivity.mStepsSharedPreference.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY,0);
            walkActivity.mStepsTaken = walkActivity.mCurrSteps - walkActivity.mStartSteps;
            Log.d(TAG, "start steps: " + walkActivity.mStartSteps);
            Log.d(TAG, "curr steps: " + walkActivity.mCurrSteps);
            Log.d(TAG, "taken steps: " + walkActivity.mStepsTaken);

            walkActivity.mStepsView.setText(Long.toString(walkActivity.mStepsTaken));


            // Calculate the user's total miles
            StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
            walkActivity.mMiles = converter.getNumMiles(walkActivity.mStepsTaken);
            //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
            walkActivity.mMiles = Math.round(walkActivity.mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
            walkActivity.mMilesView.setText("That's " + walkActivity.mMiles + " miles so far");
        }
    }

    public static void setIgnoreTimer(boolean ignoreTimer){
        WalkActivity.ignoreTimer = ignoreTimer;
    }


}
