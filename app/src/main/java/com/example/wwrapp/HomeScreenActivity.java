package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.wwrapp.fitness.FitnessService;
import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity {
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private FitnessService fitnessService;

    private TextView stepView = findViewById(R.id.homeStepsTextView);
    private TextView mileView = findViewById(R.id.homeMilesTextView);

    public long total_steps, total_miles, steps, miles;
    public int height_feet, height_inch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this,MainActivity.class);
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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fitnessService.updateStepCount();

        fitnessService.setup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
                getTotalStepsAndMiles();
                displayStepsAndMiles();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }
    //implement after getting real data
    private boolean checkHasHeight(){
        SharedPreferences saveHeight = getSharedPreferences("user_height", MODE_PRIVATE);
        String testVal = saveHeight.getString("height_feet","");
        return !testVal.equals("");
    }

    public void displayStepsAndMiles() {
        stepView.setText(String.valueOf(total_steps));
        mileView.setText(String.valueOf(total_miles));
    }

    public long stepsToMiles(long steps){
        long miles;
        int total_height = height_feet*12 +height_inch;
        miles = (long) ((total_height * .413 * steps) / 5280);
        return miles;
    }

    public void getTotalStepsAndMiles(){
        SharedPreferences totalSteps = getSharedPreferences("user_steps",MODE_PRIVATE);
        total_steps = totalSteps.getLong("total_steps",0);
        total_miles = stepsToMiles(total_steps);
    }
}

