package com.example.wwrapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.fitness.IFitnessObserver;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MockWalkActivity extends AppCompatActivity implements IFitnessObserver {

    private static final String TAG = "MockWalkActivity";
    public static String INVALID_TIME_TOAST = "Please enter a valid time";

    private TextView mHoursTextView, mMinutesTextView, mSecondsTextView, mStepsView, mMilesView;
    private Button mStopBtn, mAddStepsBtn, mSetMsBtn;
    private TimerTask mWalkTimer;
    private EditText mTimeField;


    private long mSteps = 0, mTotalSteps = 0;
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
    private long mMockTime = -1;

    private static boolean isMockingWalk = false;

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
        mTimeField = findViewById(R.id.mock_setTimeField);
        mSetMsBtn = findViewById(R.id.mock_setTimeButton);
        mTimeField = findViewById(R.id.mock_setTimeField);
        mWalkTimer = new TimerTask(this);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Stop button pressed");
                //Stop Timer
                mWalkTimer.cancel(false);
                saveData();
                returnToHomeActivity();
                finish();
            }
        });
        mSetMsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "set MS time button pressed");
                mMockTime = Long.parseLong(mTimeField.getText().toString());
                Log.d(TAG, "time set to " + mMockTime + "ms");
                if( mMockTime < 0 ) {
                    Toast.makeText(MockWalkActivity.this,
                            INVALID_TIME_TOAST
                            , Toast.LENGTH_LONG).show();
                    mMockTime = -1;
                }
            }
        });

        mAddStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add 500 Steps button clicked");
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
        Log.d(TAG, "onPause called");
        if (!mWalkTimer.isCancelled()) {
            mWalkTimer.cancel(false);
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
     * Launches the activity to enter walk information
     */
    public void launchHomeScreenActivity() {
        Log.d(TAG,"launchHomeScreenActivity called");
        // Pass the Walk data onto the next Activity
        String duration = String.format("%d hours, %d minutes, %d seconds", mHours, mMinutes, mSeconds);
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.putExtra(WWRConstants.EXTRA_FITNESS_SERVICE_VERSION_KEY, WWRConstants.MOCK_FITNESS_SERVICE_VERSION);
        Log.d(TAG, "mHours is" + mHours);
        Log.d(TAG, "mMinutes is " + mMinutes);
        Log.d(TAG, "mSeconds is " + mSeconds);
        Log.d(TAG, "mSteps is " + mSteps);
        Log.d(TAG, "mMiles is " + mMiles);
        Log.d(TAG, "Time is " + mMockTime + "ms");
        startActivity(intent);
        finish();
    }

    public void saveData() {
        Log.d(TAG, "savedData called");
        //Update total steps
        long oldSteps = mStepsSharedPreference.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);
        long currStpes = oldSteps + mSteps;
        SharedPreferences.Editor editor = mStepsSharedPreference.edit();
        editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, currStpes);
        editor.apply();

        // Updates most recent walk
        if (isMockingWalk) {
            SharedPreferences lastWalkSharedPreference = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor lastWalkEditor = lastWalkSharedPreference.edit();

            long lastSteps = lastWalkSharedPreference.getLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, 0);
            long currLastSteps = lastSteps + mSteps;
            lastWalkEditor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, currLastSteps);

            float lastMiles = lastWalkSharedPreference.getFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, 0);
            float currLastMiles = lastMiles + ((float) mMiles);
            lastWalkEditor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, currLastMiles);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN_DETAILED);
            String formattedDate = LocalDateTime.now().format(dateTimeFormatter);
            lastWalkEditor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, formattedDate);
            lastWalkEditor.apply();
        }

        mTotalSteps += mSteps;
        mSteps = 0;

        SharedPreferences timeSharedPreferences =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_SYSTEM_TIME_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor timeEditor = timeSharedPreferences.edit();
        Log.d(TAG, "Mock time is: " + mMockTime);
        // If the set text is -1
        if (mMockTime == -1) {
            timeEditor.putLong(WWRConstants.SHARED_PREFERENCES_SYSYTEM_TIME_KEY, WWRConstants.NO_MOCK_TIME);
        } else {
            timeEditor.putLong(WWRConstants.SHARED_PREFERENCES_SYSYTEM_TIME_KEY, mMockTime);
        }
        timeEditor.apply();
        Log.d(TAG, "Data Saved to Shared Preferences");
    }

    @Override
    public void update(long steps) {
        Log.d(TAG, "IFintess Implimentation: update called");
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
        Log.d(TAG, "updateViews called");
        mStepsView.setText(Long.toString(mTotalSteps));
        Log.d(TAG, "Feet: " + feet);
        Log.d(TAG, "Inches: " + inches);
        // Calculate the user's total miles
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        mMiles = converter.getNumMiles(mTotalSteps);
        //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
        mMiles = Math.round(mMiles * TENTHS_PLACE_ROUNDING_FACTOR) / TENTHS_PLACE_ROUNDING_FACTOR;
        mMilesView.setText("That's " + mMiles + " miles so far");
    }

    private static class TimerTask extends AsyncTask<String, String, String> {
        private long time;
        private int feet;
        private int inches;

        private WeakReference<MockWalkActivity> mockWalkActivityWeakReference;

        public TimerTask(MockWalkActivity context) {
            mockWalkActivityWeakReference = new WeakReference<>(context);
        }

        @Override
        public void onPreExecute() {
            Log.d(TAG, "TimerTask: onPreExecute called");
            MockWalkActivity mockWalkActivity = mockWalkActivityWeakReference.get();
            SharedPreferences heightSharedPref =
                    mockWalkActivity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
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
            // implemented from IFitnessObserver
            // updateSteps();
        }

        private void updateTime() {
            Log.d(TAG, "TimerTask: updateTime called");
            MockWalkActivity mockWalkActivity = mockWalkActivityWeakReference.get();
            mockWalkActivity.mHours = (int) (time / NUM_SECONDS_PER_HOUR);
            mockWalkActivity.mMinutes = (int) ((time / NUM_SECONDS_PER_MINUTE) % NUM_SECONDS_PER_MINUTE);
            mockWalkActivity.mSeconds = (int) (time % NUM_SECONDS_PER_MINUTE);
            Log.d(TAG, "time is " + time);
            mockWalkActivity.mHoursTextView.setText(mockWalkActivity.mHours + " hr");
            mockWalkActivity.mMinutesTextView.setText(mockWalkActivity.mMinutes + " min");
            mockWalkActivity.mSecondsTextView.setText(mockWalkActivity.mSeconds + " sec");
        }
    }

    private void returnToHomeActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.putExtra(WWRConstants.EXTRA_FITNESS_SERVICE_VERSION_KEY, WWRConstants.MOCK_FITNESS_SERVICE_VERSION);
        setResult(RESULT_OK, intent);
        HomeScreenActivity.IS_MOCKING = true;
    }
}
