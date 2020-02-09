package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Launched after the user ends a walk initiated from the Home screen; prompts user to enter
 * notes about the walk.
 */
public class EnterWalkInformationActivity extends AppCompatActivity {
    private static String TAG = "EnterWalkInformationActivity";
    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_walk_information);

        final EditText routeName = findViewById(R.id.route_name_edit_text);
        EditText startingPoint = findViewById(R.id.starting_point_edit_text);

        Button doneBtn = findViewById(R.id.enter_walk_info_done_button);

        doneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If the user hasn't entered a route name
                if (routeName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), ENTER_ROUTE_NAME_TOAST, Toast.LENGTH_LONG).show();
                } else {
                    int hours = getIntent().getIntExtra(WalkActivity.HOURS_KEY, -1);
                    int minutes = getIntent().getIntExtra(WalkActivity.MINUTES_KEY, -1);
                    int seconds = getIntent().getIntExtra(WalkActivity.SECONDS_KEY, -1);
                    Log.d(TAG, "hours is" + hours);
                    Log.d(TAG, "minutes is" + minutes);
                    Log.d(TAG, "seconds is" + seconds);
                    // Save data and go to routes screen
                    Toast.makeText(getApplicationContext(), "Save data and go to routes screen", Toast.LENGTH_LONG).show();
                    launchRoutesActivity();
                }
            }
        });

        Button cancelBtn = findViewById(R.id.enter_walk_info_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to home screen
                Toast.makeText(getApplicationContext(), "Save data and go to home screen", Toast.LENGTH_LONG).show();
                launchHomeActivity();
            }
        });
    }

    /**
     * Takes the user to the Routes screen after they've entered information for the walk
     */
    public void launchRoutesActivity() {
        Intent intent = new Intent(this, RoutesActivity.class);
        // Clear the activity stack so only the Home screen will be left
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Returns the user to the Home screen if they don't want to enter route information
     */
    public void launchHomeActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        // Clear the activity stack so only the Home screen will be left
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
