package com.example.wwrapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class HeightScreenActivity extends AppCompatActivity {

    private Spinner feetSpinner;
    private Spinner inchSpinner;
    private Button done;
    private static final String[] feet = {"", "0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] inch = {"", "0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private int userFeet;
    private int userInch;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_page);
        done = (Button) findViewById(R.id.height_button);

        feetSpinner = (Spinner) findViewById(R.id.spinner_feet);

        final ArrayAdapter<String> feetAdapter = new ArrayAdapter<String>(HeightScreenActivity.this,
                android.R.layout.simple_spinner_item, feet);

        inchSpinner = (Spinner) findViewById(R.id.spinner_inch);

        ArrayAdapter<String> inchAdapter = new ArrayAdapter<String>(HeightScreenActivity.this,
                android.R.layout.simple_spinner_item, inch);

        feetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        feetSpinner.setAdapter(feetAdapter);

        inchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inchSpinner.setAdapter(inchAdapter);

        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inches = inchSpinner.getSelectedItem().toString();
                String feet = feetSpinner.getSelectedItem().toString();
                if(inches.equals("") || feet.equals("")) {
                    Toast.makeText(HeightScreenActivity.this,
                            "Please enter a valid height"
                            , Toast.LENGTH_LONG).show();
                }
                else {
                    SharedPreferences saveHeight = getSharedPreferences("user_height", MODE_PRIVATE);
                    SharedPreferences.Editor editor = saveHeight.edit();
                    editor.putString("height_inch", inches);
                    editor.putString("height_feet", feet);
                    editor.apply();
                    Toast.makeText(HeightScreenActivity.this,
                            "Saved: Your height is " + feet + "\' " + inches + "\""
                            , Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
