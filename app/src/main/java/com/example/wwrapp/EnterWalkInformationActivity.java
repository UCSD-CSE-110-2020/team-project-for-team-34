package com.example.wwrapp;

import android.app.Activity;
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

import com.example.wwrapp.model.Route;
import com.example.wwrapp.model.RouteBuilder;
import com.example.wwrapp.model.Walk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Launched after the user ends a walk initiated from the Home screen; prompts user to enter
 * notes about the walk.
 */
public class EnterWalkInformationActivity extends AppCompatActivity {
    private static final String TAG = "EnterWalkInformationActivity";

    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";

    private Button mDoneButton;
    private Button mCancelButton;

    private String mRouteName;
    private String mStartingPoint;
    private List<String> mTags;
    private boolean mFavorite;
    private String mNotes;

    private RadioGroup mRouteShapeRadioGroup;
    private RadioGroup mRouteElevationRadioGroup;
    private RadioGroup mRouteEnvironmentRadioGroup;
    private RadioGroup mRouteSmoothnessRadioGroup;
    private RadioGroup mRouteDifficultyRadioGroup;
    private RadioGroup mRouteFavoriteRadioGroup;
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

        mDoneButton = findViewById(R.id.enter_walk_info_done_button);
        mRouteShapeRadioGroup = findViewById(R.id.route_shape_radio_group);
        mRouteElevationRadioGroup = findViewById(R.id.route_elevation_radio_group);
        mRouteEnvironmentRadioGroup = findViewById(R.id.route_environment_radio_group);
        mRouteSmoothnessRadioGroup = findViewById(R.id.route_smoothness_radio_group);
        mRouteDifficultyRadioGroup = findViewById(R.id.route_difficulty_radio_group);
        mRouteFavoriteRadioGroup = findViewById(R.id.route_favorite_radio_group);

        mRouteShapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        // Register the done button
        mDoneButton.setOnClickListener(new View.OnClickListener() {
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
                    if (mRouteFavoriteRadioGroup.getCheckedRadioButtonId() != -1) {
                        int id = mRouteFavoriteRadioGroup.getCheckedRadioButtonId();
                        mRouteFavoriteRadioBtn = findViewById(id);
                        if (mRouteFavoriteRadioBtn.isChecked()) {
                            mFavorite = true;
                        } else {
                            mFavorite = false;
                        }
                    }

                    //  Get and save the notes
                    EditText editText = findViewById(R.id.notes_edit);
                    mNotes = editText.getText().toString();
                    Log.d(TAG, "Notes are: " + mNotes);


                    Log.d(TAG, "Favorite is: " + mFavorite);

                    mRouteName = routeName.getText().toString();
                    mStartingPoint = startingPoint.getText().toString();

                    // Determine how to end this activity
                    handleDoneButtonClick();
                }
            }
        });

        mCancelButton = findViewById(R.id.enter_walk_info_cancel_button);
        // Register the cancel button
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String callerID = intent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
                switch (callerID) {
                    case WWRConstants.EXTRA_ROUTES_ACTIVITY_CALLER_ID:
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                    case WWRConstants.EXTRA_WALK_ACTIVITY_CALLER_ID:
                        finish();
                        break;
                }
                finish();
            }
        });
    }

    private void handleDoneButtonClick() {
        Intent intent = getIntent();
        // Check who started this activity
        String callerID = intent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
        switch (callerID) {
            case WWRConstants.EXTRA_WALK_ACTIVITY_CALLER_ID:
                startRoutesActivity();
                break;
            case WWRConstants.EXTRA_ROUTES_ACTIVITY_CALLER_ID:
                returnToRoutesActivity();
                break;
        }
        Log.d(TAG, "End of method handleDoneButtonClick");
        finish();
    }

    private void returnToRoutesActivity() {
        Log.d(TAG, "In method returnToRoutesActivity");
        Intent returnIntent = new Intent();
        // Create a new route, but without any walk stats
        RouteBuilder routeBuilder = new RouteBuilder();
        Route route = routeBuilder.setRouteName(mRouteName)
                .setStartingPoint(mStartingPoint)
                .setSteps(0)
                .setMiles(0.0)
                .setNotes(mNotes)
                .setTags(mTags)
                .setFavorite(mFavorite)
                .setDuration(null)
                .getRoute();

        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        returnIntent.putExtra(WWRConstants.EXTRA_MANUALLY_CREATED_ROUTE_KEY, true);
        returnIntent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID);

        // returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass this Intent back
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    /**
     * Takes the user to the Routes screen after they've entered information for the walk
     */
    public void startRoutesActivity() {
        // Get the data from the Walk
        Intent incomingIntent = getIntent();
        String callerID = incomingIntent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
        Intent outgoingIntent;

        // The route object to be passed on
        Route route;

        switch (callerID) {
            case WWRConstants.EXTRA_WALK_ACTIVITY_CALLER_ID:
                // Retrieve the Walk stats
                Walk walk = (Walk) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));
                long walkSteps = walk.getSteps();
                double walkMiles = walk.getMiles();
                Date walkDate = walk.getDate();
                String duration = walk.getDuration();

                Log.d(TAG, walk.toString());

                // Bundle up data to pass to the Routes activity
                outgoingIntent = new Intent(EnterWalkInformationActivity.this, RoutesActivity.class);

                // Construct the Route
                RouteBuilder routeBuilder = new RouteBuilder();
                route = routeBuilder.setRouteName(mRouteName)
                        .setStartingPoint(mStartingPoint)
                        .setSteps(walkSteps)
                        .setMiles(walkMiles)
                        .setTags(mTags)
                        .setNotes(mNotes)
                        .setFavorite(mFavorite)
                        .setDuration(duration)
                        .getRoute();


                outgoingIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
                outgoingIntent.putExtra(WWRConstants.EXTRA_MANUALLY_CREATED_ROUTE_KEY, false);
                // Let the RoutesActivity know who launched it
                outgoingIntent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                        WWRConstants.EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID);

                // Clear the activity stack so only the Home screen will be left
                outgoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(outgoingIntent);
                finish();
                break;
            default:
                Log.d(TAG, "Caller is unhandled: " + callerID);
        }


    }

    /**
     * Returns the user to the Home screen if they don't want to enter route information
     */
    public void startHomeActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        // Clear the activity stack so only the Home screen will be left
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
