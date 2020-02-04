package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity {

    private Button startNewWalkBtn = findViewById(R.id.startNewWalkButton);
    private TextView stepView = findViewById(R.id.homeStepsTextView);
    private TextView mileView = findViewById(R.id.homeMilesTextView);
    private int steps,miles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!checkHasHeight()){
            Intent askHeight = new Intent(HomeScreenActivity.this,MainActivity.class);
            startActivity(askHeight);
        }

        final Button startNewWalkBtn = findViewById(R.id.startNewWalkButton);
        startNewWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walk = new Intent(HomeScreenActivity.this,WalkActivity.class);
                startActivity(walk);
            }
        });

    }


    //implement after getting real data
    private boolean checkHasHeight(){
        //if data exist
        boolean hasHeight = true;
        //else if data doesn't exist
        //boolean hasHeight = false;
        return hasHeight;
    }

    private void displayInfo(int steps,int miles){
        stepView.setText(steps);
        mileView.setText(miles);
    }

    //read data files, implement after having data type
    private void readStepsAndMiles(int steps,int miles){
        return;
    }
}

