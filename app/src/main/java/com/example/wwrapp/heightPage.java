package com.example.wwrapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class heightPage extends AppCompatActivity {

    private Spinner feetSpinner;
    private Spinner inchSpinner;
    private static final String[] feet = {"", "0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] inch = {"", "0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private int userFeet;
    private int userInch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        feetSpinner = (Spinner) findViewById(R.id.spinner_feet);

        final ArrayAdapter<String> feetAdapter = new ArrayAdapter<String>(heightPage.this,
                android.R.layout.simple_spinner_item, feet);

        inchSpinner = (Spinner) findViewById(R.id.spinner_inch);

        ArrayAdapter<String> inchAdapter = new ArrayAdapter<String>(heightPage.this,
                android.R.layout.simple_spinner_item, inch);

        feetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        feetSpinner.setAdapter(feetAdapter);

        inchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inchSpinner.setAdapter(inchAdapter);

        inchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == 1){
                    userInch = 0;
                } else {
                    userInch = Integer.valueOf(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userInch = 0;
            }
        });

        feetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == 1){
                    if(userInch == 0){
                        // show a pop up alert asking user to enter a non-zero feet.
                    } else {
                        userFeet = 0;
                    }
                } else {
                    userFeet = Integer.valueOf(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(userInch == 0){
                    // show a pop up alert asking user to enter a proper feet that is > 0.
                }
            }
        });

    }



}
