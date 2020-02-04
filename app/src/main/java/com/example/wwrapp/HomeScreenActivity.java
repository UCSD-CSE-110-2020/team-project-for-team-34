package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.wwrapp.fitness.FitnessService;
import com.example.wwrapp.fitness.FitnessServiceFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity {
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";
    private TextView textSteps;
    private FitnessService fitnessService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textSteps = findViewById(R.id.homeStepsTextView);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fitnessService.updateStepCount();

        fitnessService.setup();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this,MainActivity.class);
            startActivity(askHeight);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }
    //implement after getting real data
    private boolean checkHasHeight(){
        //if data exist
        boolean hasHeight = true;
        //else if data doesn't exist
        //boolean hasHeight = false;
        return hasHeight;
    }

    public void setStepCount(long totalStep) {
        textSteps.setText(String.valueOf(totalStep));
    }
}

