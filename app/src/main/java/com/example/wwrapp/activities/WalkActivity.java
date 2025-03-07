package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.fitness.FitnessApplication;
import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Walk;
import com.example.wwrapp.models.WalkBuilder;
import com.example.wwrapp.utils.DateTimeUtils;
import com.example.wwrapp.utils.StepsAndMilesConverter;
import com.example.wwrapp.utils.WWRConstants;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private boolean mIsObserving;

    private LocalDateTime mDateTime;
    private AbstractUser mUser;
    private String mFitnessServiceKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        // Get the user
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        // Get the service type
        mFitnessServiceKey = getIntent().getStringExtra(WWRConstants.EXTRA_FITNESS_SERVICE_TYPE_KEY);

        startObservingFitnessService(mFitnessServiceKey);

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
            Intent intent = new Intent(WalkActivity.this, EnterWalkInformationActivity.class);
            startActivity(intent);
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
    protected void onStart() {
        super.onStart();

        if (!mIsObserving) {
            startObservingFitnessService(mFitnessServiceKey);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }

        // Stop observing the fitness service
        if (mIsObserving) {
            stopObservingFitnessService(mFitnessServiceKey);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
        }
    }

    /**
     * Decides what to do when the walk is stopped
     */
    private void handleWalkStopped() {
        Log.d(TAG, "handleWalkStopped called");
        // Check which activity started this current one:
        Intent incomingIntent = getIntent();
        String callerId = incomingIntent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);

        switch(callerId) {
            case WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID:
                startEnterWalkInformationActivity();
                break;
            case WWRConstants.EXTRA_ROUTE_DETAIL_ACTIVITY_CALLER_ID:
                returnToRouteDetailActivity();
                break;
            case WWRConstants.EXTRA_TEAM_ROUTE_DETAIL_ACTIVITY_CALLER_ID:
                returnToTeamRouteDetailActivity();
                break;
        }
        // Close up this activity
        finish();
    }

    /**
     * Launches the activity to enter walk information
     */
    private void startEnterWalkInformationActivity() {
        Log.d(TAG, "startEnterWalkInformationActivity called");
        // Pass the Walk data onto the next Activity
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);

        // Use mocked time, if mocking
        if (FitnessApplication.getDummyFitnessServiceInstance().isMockingTime()) {
            mDateTime = DateTimeUtils.getDateTimeFromMillis(FitnessApplication.getDummyFitnessServiceInstance().getTime());
        }

        // Convert LocalDateTime to String
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN_DETAILED);
        String formattedDate = mDateTime.format(dateTimeFormatter);

        Log.d(TAG, "Original Walk date  = " + formattedDate);

        // Create a Walk
        WalkBuilder walkBuilder = new WalkBuilder();
        Walk walk = walkBuilder.setSteps(mStepsTaken)
                .setMiles(mMiles)
                .setDate(formattedDate)
                .setDuration(duration)
                .getWalk();

        // Pass Walk and user as extras
        Intent intent = new Intent(this, EnterWalkInformationActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_WALK_ACTIVITY_CALLER_ID);
        intent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);

        startActivity(intent);
    }

    /**
     * Returns data to the RoutesDetailActivity
     */
    private void returnToTeamRouteDetailActivity() {
        Log.d(TAG, "returnToTeamRouteDetail called");

        // Create a new Walk
        // TODO: Test that a walk is updated with a dummy number of steps.
        // TODO: Remove the dummy steps in production.
//        mStepsTaken = 100;

        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);

        // Use mocked time, if mocking
        if (FitnessApplication.getDummyFitnessServiceInstance().isMockingTime()) {
            mDateTime = DateTimeUtils.getDateTimeFromMillis(FitnessApplication.getDummyFitnessServiceInstance().getTime());
        }

        // Convert LocalDateTime to String
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN_DETAILED);
        String formattedDate = mDateTime.format(dateTimeFormatter);
        Log.d(TAG, "formatted date is " + formattedDate);

        // Create the walk
        WalkBuilder walkBuilder = new WalkBuilder();
        Walk walk = walkBuilder.setSteps(mStepsTaken)
                .setMiles(mMiles)
                .setDate(formattedDate)
                .setDuration(duration)
                .getWalk();

        Log.d(TAG, "Walk object returned to RouteDetail is\n" + walk.toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        // Pass this Intent back
        setResult(Activity.RESULT_OK, returnIntent);
    }

    /**
     * Returns data to the RoutesDetailActivity
     */
    private void returnToRouteDetailActivity() {
        Log.d(TAG, "returnToRouteDetail called");

        // Create a new Walk
        // TODO: Test that a walk is updated with a dummy number of steps.
        // TODO: Remove the dummy steps in production.
//        mStepsTaken = 100;

        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);

        // Use mocked time, if mocking
        if (FitnessApplication.getDummyFitnessServiceInstance().isMockingTime()) {
            mDateTime = DateTimeUtils.getDateTimeFromMillis(FitnessApplication.getDummyFitnessServiceInstance().getTime());
        }

        // Convert LocalDateTime to String
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN_DETAILED);
        String formattedDate = mDateTime.format(dateTimeFormatter);
        Log.d(TAG, "formatted date is " + formattedDate);

        // Create the walk
        WalkBuilder walkBuilder = new WalkBuilder();
        Walk walk = walkBuilder.setSteps(mStepsTaken)
                .setMiles(mMiles)
                .setDate(formattedDate)
                .setDuration(duration)
                .getWalk();


        Log.d(TAG, "Walk object returned to RouteDetail is\n" + walk.toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        // TODO: Need to pass user back here?
        // Pass this Intent back
        setResult(Activity.RESULT_OK, returnIntent);
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

    private void startObservingFitnessService(String fitnessServiceKey) {
        mIsObserving = true;

        // Provide a default implementation if key is null
        if (fitnessServiceKey == null) {
            // If the factory key is null, use the DummyFitnessService by default:
            IFitnessService defaultFitnessService = FitnessServiceFactory.createFitnessService(WWRConstants.DEFAULT_FITNESS_SERVICE_FACTORY_KEY);

            // Down-cast the fitness service so we can add observers to it and start it.
            ((IFitnessSubject) defaultFitnessService).registerObserver(this);
            defaultFitnessService.startFitnessService(this);

            // Provide a value for the key so that we know in onResume() that we've already started
            // the service
            mFitnessServiceKey = WWRConstants.DEFAULT_FITNESS_SERVICE_FACTORY_KEY;
            return;
        }

        IFitnessService fitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
        ((IFitnessSubject) fitnessService).registerObserver(this);
        fitnessService.startFitnessService(this);

    }

    private void stopObservingFitnessService(String fitnessServiceKey) {
        IFitnessService fitnessService = FitnessServiceFactory.createFitnessService(mFitnessServiceKey);
        ((IFitnessSubject) fitnessService).removeObserver(this);
        mIsObserving = false;

    }


}
