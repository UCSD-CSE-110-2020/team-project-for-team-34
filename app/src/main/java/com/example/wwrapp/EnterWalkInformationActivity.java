package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

/**
 * Launched after the user ends a walk initiated from the Home screen; prompts user to enter
 * notes about the walk.
 */
public class EnterWalkInformationActivity extends AppCompatActivity {
    public static final String CALLER_ID_KEY = "callerID";
    public static final String ROUTE_MILES_KEY = "miles";
    public static final String ROUTE_STEPS_KEY = "steps";
    public static final String ROUTE_NAME_KEY = "routeName";
    public static final String ROUTE_STARTING_POINT_KEY = "startingPoint";
    public static final String ROUTE_DATE_KEY = "date";
    public static final String ROUTE_DURATION_KEY = "duration";
    public static final String CALLER_ID = "EnterWalkInformation";

    private static String TAG = "EnterWalkInformationActivity";
    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";

    private String mRouteName;
    private String mStartingPoint;

    private RadioGroup group1;
    private RadioGroup group2;
    private RadioGroup group3;
    private RadioGroup group4;
    private RadioGroup group5;
    private RadioButton loop;
    private RadioButton flat;
    private RadioButton streets;
    private RadioButton even;
    private RadioButton difficulty;
    private boolean favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_walk_information);

        final EditText routeName = findViewById(R.id.route_name_edit_text);
        final EditText startingPoint = findViewById(R.id.starting_point_edit_text);

        Button doneBtn = findViewById(R.id.enter_walk_info_done_button);
        group1 = findViewById(R.id.group1);
        group2 = findViewById(R.id.group2);
        group3 = findViewById(R.id.group3);
        group4 = findViewById(R.id.group4);
        group5 = findViewById(R.id.group5);

        doneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If the user hasn't entered a route name
                if (routeName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), ENTER_ROUTE_NAME_TOAST, Toast.LENGTH_LONG).show();
                } else {
                    // extract info from radio group
                    if(group1.getCheckedRadioButtonId() != -1){
                        int id = group1.getCheckedRadioButtonId();
                        loop = findViewById(id);

                        // data storing here ...
                    }
                    if(group2.getCheckedRadioButtonId() != -1){
                        int id = group2.getCheckedRadioButtonId();
                        flat = findViewById(id);

                        // data storing here ...
                    }
                    if(group1.getCheckedRadioButtonId() != -1){
                        int id = group3.getCheckedRadioButtonId();
                        streets = findViewById(id);

                        // data storing here ...
                    }
                    if(group1.getCheckedRadioButtonId() != -1){
                        int id = group4.getCheckedRadioButtonId();
                        even = findViewById(id);

                        // data storing here ...
                    }
                    if(group1.getCheckedRadioButtonId() != -1){
                        int id = group5.getCheckedRadioButtonId();
                        difficulty = findViewById(id);

                        // data storing here ...
                    }



                    // Save data and go to routes screen
                    mRouteName = routeName.getText().toString();
                    mStartingPoint = startingPoint.getText().toString();
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
     * This method is required for radio button
     */
    public void checkButton(View view){
        // can do nothing or store data here instead...

        // check for favorite radio button only, rest on top...
        RadioButton favoriteButton = findViewById(R.id.favorite);
        if(favoriteButton.isSelected()){
            favorite = true;
        } else {
            favorite = false;
        }
    }


    /**
     * Takes the user to the Routes screen after they've entered information for the walk
     */
    public void launchRoutesActivity() {
        Intent intent = new Intent(this, RoutesActivity.class);

        // Get the data to be saved for Routes
        int hours = getIntent().getIntExtra(WalkActivity.HOURS_KEY, -1);
        int minutes = getIntent().getIntExtra(WalkActivity.MINUTES_KEY, -1);
        int seconds = getIntent().getIntExtra(WalkActivity.SECONDS_KEY, -1);
        String duration = String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);

        LocalDateTime localDateTime = LocalDateTime.now();
        intent.putExtra(EnterWalkInformationActivity.ROUTE_DATE_KEY, localDateTime);

        intent.putExtra(EnterWalkInformationActivity.ROUTE_DURATION_KEY, duration);
        intent.putExtra(EnterWalkInformationActivity.ROUTE_NAME_KEY, mRouteName);
        intent.putExtra(EnterWalkInformationActivity.ROUTE_STARTING_POINT_KEY, mStartingPoint);

        int steps = (int) (getIntent().getLongExtra(WalkActivity.STEPS_KEY, 0));
        double miles = getIntent().getDoubleExtra(WalkActivity.MILES_KEY, 0);

        Log.d(TAG, "Steps are: " + steps);
        Log.d(TAG, "Miles are: " + miles);


        intent.putExtra(EnterWalkInformationActivity.ROUTE_STEPS_KEY, steps);
        intent.putExtra(EnterWalkInformationActivity.ROUTE_MILES_KEY, miles);

        intent.putExtra(EnterWalkInformationActivity.CALLER_ID_KEY, EnterWalkInformationActivity.CALLER_ID);


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
