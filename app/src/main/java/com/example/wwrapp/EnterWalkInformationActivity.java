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

import com.example.wwrapp.database.Route;
import com.example.wwrapp.database.Walk;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO: Make sure that default values for radio buttons aren't selected (right now they are)
// TODO: Allow the user to uncheck radio buttons if they change their mind.

/**
 * Launched after the user ends a walk initiated from the Home screen; prompts user to enter
 * notes about the walk.
 */
public class EnterWalkInformationActivity extends AppCompatActivity {
    private static final String TAG = "EnterWalkInformationActivity";

    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";


    private String mRouteName;
    private String mStartingPoint;
    private List<String> mTags;
    private boolean mRouteFavorite;
    private String mNotes;

    private RadioGroup mRouteShapeRadioGroup;
    private RadioGroup mRouteElevationRadioGroup;
    private RadioGroup mRouteEnvironmentRadioGroup;
    private RadioGroup mRouteSmoothnessRadioGroup;
    private RadioGroup mRouteDifficultyRadioGroup;
    private RadioButton mRouteShapeRadioBtn;
    private RadioButton mRouteElevationRadioBtn;
    private RadioButton mRouteEnvironmentRadioBtn;
    private RadioButton mRouteSmoothnessRadioBtn;
    private RadioButton mRouteDifficultyRadioBtn;
    private RadioButton mRouteFavoriteRadioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_walk_information);

        // Create the list of tags
        mTags = new ArrayList<>();

        final EditText routeName = findViewById(R.id.route_name_edit_text);
        final EditText startingPoint = findViewById(R.id.starting_point_edit_text);

        Button doneBtn = findViewById(R.id.enter_walk_info_done_button);
        mRouteShapeRadioGroup = findViewById(R.id.route_shape_radio_group);
        mRouteElevationRadioGroup = findViewById(R.id.route_elevation_radio_group);
        mRouteEnvironmentRadioGroup = findViewById(R.id.route_environment_radio_group);
        mRouteSmoothnessRadioGroup = findViewById(R.id.route_smoothness_radio_group);
        mRouteDifficultyRadioGroup = findViewById(R.id.route_difficulty_radio_group);

        mRouteShapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If the user hasn't entered a route name
                if (routeName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), ENTER_ROUTE_NAME_TOAST, Toast.LENGTH_LONG).show();
                } else {
                    // extract info from radio group
                    if (mRouteShapeRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteShapeRadioGroup.getCheckedRadioButtonId();
                        mRouteShapeRadioBtn = findViewById(id);
                        String shapeTag = mRouteShapeRadioBtn.getText().toString();
                        Log.d(TAG, "Shape is: " + shapeTag);
                        // Save this tag
                        mTags.add(shapeTag);
                    }
                    if (mRouteElevationRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteElevationRadioGroup.getCheckedRadioButtonId();
                        mRouteElevationRadioBtn = findViewById(id);
                        String elevationTag = mRouteElevationRadioBtn.getText().toString();
                        Log.d(TAG, "Elevation is: " + elevationTag);
                        mTags.add(elevationTag);
                    }
                    if (mRouteEnvironmentRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteEnvironmentRadioGroup.getCheckedRadioButtonId();
                        mRouteEnvironmentRadioBtn = findViewById(id);
                        String environmentTag = mRouteEnvironmentRadioBtn.getText().toString();
                        Log.d(TAG, "Environment is: " + environmentTag);
                        mTags.add(environmentTag);
                    }
                    if (mRouteSmoothnessRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteSmoothnessRadioGroup.getCheckedRadioButtonId();
                        mRouteSmoothnessRadioBtn = findViewById(id);
                        String smoothnessTag = mRouteSmoothnessRadioBtn.getText().toString();
                        Log.d(TAG, "Smoothness is: " + smoothnessTag);
                        mTags.add(smoothnessTag);
                    }
                    if (mRouteDifficultyRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteDifficultyRadioGroup.getCheckedRadioButtonId();
                        mRouteDifficultyRadioBtn = findViewById(id);
                        String difficultyTags = mRouteDifficultyRadioBtn.getText().toString();
                        Log.d(TAG, "Difficulty is: " + difficultyTags);
                        mTags.add(difficultyTags);
                    }

                    //  Get and save the notes
                    EditText editText = findViewById(R.id.notes_edit);
                    mNotes = editText.getText().toString();
                    Log.d(TAG, "Notes are: " + mNotes);


                    // Get favorite/not favorite
                    checkButton(v);
                    Log.d(TAG, "Favorite is: " + mRouteFavorite);

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
    public void checkButton(View view) {
        // can do nothing or store data here instead...

        // check for mRouteFavorite radio button only, rest on top...
        mRouteFavoriteRadioBtn = findViewById(R.id.favorite);
        if (mRouteFavoriteRadioBtn.isSelected()) {
            mRouteFavorite = true;
        } else {
            mRouteFavorite = false;
        }
    }


    /**
     * Takes the user to the Routes screen after they've entered information for the walk
     */
    public void launchRoutesActivity() {
        // Get the data from the Walk
        Intent incomingIntent = getIntent();
        Walk walk = (Walk) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));
        long walkSteps = walk.getSteps();
        double walkMiles = walk.getMiles();
        LocalDateTime walkDate = walk.getDate();
        String duration = walk.getDuration();
        Log.d(TAG, "Steps are: " + walkSteps);
        Log.d(TAG, "Miles are: " + walkMiles);

        // Bundle up data to pass to the Routes activity
        Intent outgoingIntent = new Intent(this, RoutesActivity.class);
        Route route = new Route(mRouteName, mStartingPoint, walkDate, duration, walkSteps,
                walkMiles, mTags, mRouteFavorite, mNotes);
        outgoingIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);

        // Let the RoutesActivity know who launched it
        outgoingIntent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID);

        // Clear the activity stack so only the Home screen will be left
        outgoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(outgoingIntent);
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
