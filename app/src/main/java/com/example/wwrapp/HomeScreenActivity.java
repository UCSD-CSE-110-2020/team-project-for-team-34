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

public class HomeScreenActivity extends AppCompatActivity {
    private String fitnessServiceKey = "GOOGLE_FIT";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private FitnessService fitnessService;

    // SharedPreferences
    public static final String STEPS_SHARED_PREF_NAME = "user_steps";
    public static final String TOTAL_STEPS_KEY = "totalSteps";

    private TextView mStepsView;
    private TextView mMilesView;

    private long mTotalSteps;
    private double mTotalMiles;

    private FitnessAsyncTask runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mStepsView = findViewById(R.id.homeStepsTextView);
        mMilesView = findViewById(R.id.homeMilesTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreenActivity homeScreenActivity) {
                return new GoogleFitAdapter(homeScreenActivity);
            }
        });
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this, HeightScreenActivity.class);
            startActivity(askHeight);
        }


        findViewById(R.id.startNewWalkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walk = new Intent(HomeScreenActivity.this,WalkActivity.class);
                startActivity(walk);
            }
        });

        findViewById(R.id.routeScreenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent route = new Intent(HomeScreenActivity.this,RoutesActivity.class);
                startActivity(route);
            }
        });

        runner = new FitnessAsyncTask();
        fitnessService.setup();
        runner.execute("");
    }



    private class FitnessAsyncTask extends AsyncTask<String, String, String> {
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            while(!isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
                fitnessService.updateStepCount();
                publishProgress(params);
            }
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... update) {
            setTotalStepsAndMiles();
            displayStepsAndMiles();
        }

        @Override
        protected void onPostExecute(String result){
            setTotalStepsAndMiles();
            displayStepsAndMiles();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
                setTotalStepsAndMiles();
                displayStepsAndMiles();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    //implement after getting real data
    private boolean checkHasHeight(){
        SharedPreferences saveHeight =
                getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
        int testVal = saveHeight.getInt(HeightScreenActivity.HEIGHT_FEET_KEY,-1);
        // If testVal == -1, then there was no height
        return testVal != - 1;
    }

    public void displayStepsAndMiles() {
        this.mStepsView.setText(String.valueOf(this.mTotalSteps));
        this.mMilesView.setText(String.valueOf(Math.round(this.mTotalMiles * 10)/10.0));
    }


    public void setTotalStepsAndMiles(){
        // Get the total number of steps
        SharedPreferences stepsSharedPref =
                getSharedPreferences(STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        this.mTotalSteps = stepsSharedPref.getLong(TOTAL_STEPS_KEY,0);

        // Get the user's height
        SharedPreferences heightSharedPref =
                getSharedPreferences(HeightScreenActivity.HEIGHT_SHARED_PREF_NAME, MODE_PRIVATE);
        int feet = 6;
                //heightSharedPref.getInt(HeightScreenActivity.HEIGHT_FEET_KEY, 0);
        int inches = heightSharedPref.getInt(HeightScreenActivity.HEIGHT_INCHES_KEY, 0);

        // Calculate the user's total miles
        StepsAndMilesConverter converter = new StepsAndMilesConverter(feet, inches);
        this.mTotalMiles = converter.getNumMiles(this.mTotalSteps);
    }
}

