package com.example.wwrapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.wwrapp.database.Route;

import java.util.ArrayList;
import java.util.List;

public class NewRouteActivity extends AppCompatActivity {
    private static final String TAG = "NewRouteActivity";



    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";

    public static final String CALLER_ID_KEY = "callerID";
    public static final String CALLER_ID = "NewRouteInformation";
    public static final String ROUTE_KEY = "ROUTE_KEY";

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
        setContentView(R.layout.activity_new_route);

        // Create the list of tags
        mTags = new ArrayList<>();

        final EditText routeName = findViewById(R.id.new_route_name_edit_text);
        final EditText startingPoint = findViewById(R.id.new_route_starting_point_edit_text);

        mRouteShapeRadioGroup = findViewById(R.id.new_route_shape_radio_group);
        mRouteElevationRadioGroup = findViewById(R.id.new_route_elevation_radio_group);
        mRouteEnvironmentRadioGroup = findViewById(R.id.new_route_environment_radio_group);
        mRouteSmoothnessRadioGroup = findViewById(R.id.new_route_smoothness_radio_group);
        mRouteDifficultyRadioGroup = findViewById(R.id.new_route_difficulty_radio_group);


        Button cancelButton = findViewById(R.id.enter_new_route_info_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button doneButton = findViewById(R.id.enter_new_route_info_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
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
                    EditText editText = findViewById(R.id.notes_edit_new);
                    mNotes = editText.getText().toString();
                    Log.d(TAG, "Notes are: " + mNotes);


                    // Get favorite/not favorite
                    checkButton(v);
                    Log.d(TAG, "Favorite is: " + mRouteFavorite);

                    mRouteName = routeName.getText().toString();
                    mStartingPoint = startingPoint.getText().toString();
                    Toast.makeText(getApplicationContext(), "Save data and go to routes screen", Toast.LENGTH_LONG).show();

                    returnRouteActivity();
                }

            }
        });

    }

    /**
     * This method is required for radio button
     */
    public void checkButton(View view) {
        // can do nothing or store data here instead...

        // check for mRouteFavorite radio button only, rest on top...
        mRouteFavoriteRadioBtn = findViewById(R.id.favorite_new);
        if (mRouteFavoriteRadioBtn.isSelected()) {
            mRouteFavorite = true;
        } else {
            mRouteFavorite = false;
        }
    }

    public void returnRouteActivity() {

        Intent outgoingIntent = new Intent(this, RoutesActivity.class);
        Route route = new Route(mRouteName, mStartingPoint, null, null, 0,
                0, mTags, mRouteFavorite, mNotes);
        outgoingIntent.putExtra(ROUTE_KEY, route);

        outgoingIntent.putExtra(NewRouteActivity.CALLER_ID_KEY, NewRouteActivity.CALLER_ID);

        outgoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(outgoingIntent);

        finish();
    }
}


