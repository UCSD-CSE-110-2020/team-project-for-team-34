package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

public class WalkActivity extends AppCompatActivity {
    private TextView hrView, minView, secView, stepView, mileView;
    private Button stop;
    private TimerTask timer;
    private long startSteps;
    private SharedPreferences stepsSharedPref;
    public static final String STEPS_SHARED_PREF_NAME = "user_steps";
    public static final String TOTAL_STEPS_KEY = "totalSteps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        stepsSharedPref = getSharedPreferences(STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        startSteps = stepsSharedPref.getLong(TOTAL_STEPS_KEY,0);

        hrView = findViewById(R.id.hrs);
        minView = findViewById(R.id.mins);
        secView = findViewById(R.id.secs);
        stepView = findViewById(R.id.stepCount);
        mileView = findViewById(R.id.mileCount);
        stop = findViewById(R.id.stopButton);
        timer = new TimerTask();
        timer.execute();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel(false);
            }
        });
    }

    private class TimerTask extends AsyncTask<String,String, String> {
        private long time = 0;
        private int feet;
        private int inches;

        @Override
        protected String doInBackground(String ... params) {
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
            updateTime();
            updateSteps();
        }

        private void updateTime() {
            long hrTime = (time / 3600);
            long minTime = (time / 60) % 60;
            long secTime = (time) % 60;
            hrView.setText((int)hrTime + " hr");
            minView.setText((int)minTime + " min");
            secView.setText((int)secTime + " sec");
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
