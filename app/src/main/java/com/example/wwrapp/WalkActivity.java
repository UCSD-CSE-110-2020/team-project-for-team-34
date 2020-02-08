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

    private TextView hrView, minView, secView, stepView, mileView;
    private Button stop;
    private TimerTask timer;
    private long startSteps;
    private SharedPreferences stepsSharedPref;
    public static final String STEPS_SHARED_PREF_NAME = "user_steps";
    public static final String TOTAL_STEPS_KEY = "totalSteps";

    private int hours;
    private int minutes;
    private int seconds;

    public static final String HOURS_KEY = "HOURS_KEY";
    public static final String MINUTES_KEY = "MINUTES_KEY";
    public static final String SECONDS_KEY = "SECONDS_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Log.d(TAG, "onCreate called");
        stepsSharedPref = getSharedPreferences(STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        startSteps = stepsSharedPref.getLong(TOTAL_STEPS_KEY,0);

        hrView = findViewById(R.id.hrs);
        minView = findViewById(R.id.mins);
        secView = findViewById(R.id.secs);
        stepView = findViewById(R.id.stepCount);
        mileView = findViewById(R.id.mileCount);
        stop = findViewById(R.id.stopButton);
        Log.d(TAG, "Right before TimerTask");
        timer = new TimerTask();
        timer.execute();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel(false);
                // Start enter walk info
//                startWalkInfoActivity();
                finish();
            }
        });
    }

    public void startWalkInfoActivity() {
        Intent intent = new Intent(this, EnterWalkInformationActivity.class);
        intent.putExtra(HOURS_KEY, hours);
        intent.putExtra(MINUTES_KEY, minutes);
        intent.putExtra(SECONDS_KEY, seconds);
        Log.d(TAG, "hours is" + hours);
        Log.d(TAG, "minutes is" + minutes);
        Log.d(TAG, "seconds is" + seconds);
        startActivity(intent);
    }

    private class TimerTask extends AsyncTask<String,String, String> {
        private long time = 0;
        private int feet;
        private int inches;

        @Override
        protected String doInBackground(String ... params) {
            Log.d(TAG, "doInBackground called");

            while (true) {
                try {
                    Thread.sleep(1000);
                    ++time;
                    // THIS CODE IS TO SHOW STEPS CHANGING, REMOVE IN PRODUCTION CODE
                    // REMOVE ==================================================================
                    long currSteps = stepsSharedPref.getLong(TOTAL_STEPS_KEY,0);
                    ++currSteps;
                    SharedPreferences.Editor editor = stepsSharedPref.edit();
                    editor.putLong(TOTAL_STEPS_KEY, currSteps);
                    editor.apply();
                    // REMOVE ===================================================================
                    publishProgress("update Time");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }

        @Override
        public void onPreExecute() {
            SharedPreferences heightSharedPref =
                    getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
            feet = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_FEET_KEY, 0);
            inches = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_INCHES_KEY, 0);
        }

        @Override
        public void onProgressUpdate(String ... text) {
            Log.d(TAG, "onProgressUpdate called");

            updateTime();
            updateSteps();
        }

        private void updateTime() {
            Log.d(TAG, "updateTime called");

            hours = (int) (time / 3600);
            minutes = (int) ((time / 60) % 60);
            seconds = (int) (time % 60);
            Log.d(TAG, "time is " + time);
            hrView.setText(hours + " hr");
            minView.setText(minutes + " min");
            secView.setText(seconds + " sec");
        }
        private void updateSteps() {
            long currSteps = stepsSharedPref.getLong(TOTAL_STEPS_KEY,0);
            long stepsTaken = currSteps - startSteps;
            double milesTravelled;
            stepView.setText(Long.toString(stepsTaken));
            // Calculate the user's total miles
            StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
            milesTravelled = converter.getNumMiles(stepsTaken);
            //https://www.quora.com/How-can-I-round-a-number-to-1-decimal-digit-in-Java
            milesTravelled = Math.round(milesTravelled * 10) / 10.0;
            mileView.setText("That's " + milesTravelled + " miles so far");
        }
    }
}
