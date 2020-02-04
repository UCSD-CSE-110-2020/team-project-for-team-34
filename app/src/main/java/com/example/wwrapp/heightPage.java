package com.example.wwrapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class heightPage extends AppCompatActivity {

    private Spinner feetSpinner;
    private Spinner inchSpinner;
    private static final String[] feet = {"", "0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] inch = {"", "0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        feetSpinner = (Spinner) findViewById(R.id.spinner_feet);

        ArrayAdapter<String> feetAdapter = new ArrayAdapter<String>(heightPage.this,
                android.R.layout.simple_spinner_item, feet);

        inchSpinner = (Spinner) findViewById(R.id.spinner_inch);

        ArrayAdapter<String> inchAdapter = new ArrayAdapter<String>(heightPage.this,
                android.R.layout.simple_spinner_item, inch);

        feetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        feetSpinner.setAdapter(feetAdapter);

        inchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inchSpinner.setAdapter(inchAdapter);

    }



}
