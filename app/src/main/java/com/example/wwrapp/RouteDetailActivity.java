package com.example.wwrapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.wwrapp.model.Route;
import com.example.wwrapp.model.Walk;

import java.util.List;

/**
 * Detailed information page for a Route
 */
public class RouteDetailActivity extends AppCompatActivity {
    private static final String TAG = "RouteDetailActivity";
    private static final int START_EXISTING_WALK_REQUEST_CODE = 1;

    ToggleButton mFavoriteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        // Get the clicked on route
        Route route = (Route) (getIntent().getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

        // Register the "X"/close screen button
        findViewById(R.id.close_route_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close up this activity
                Log.d(TAG, "Clicked 'X' button");
                finish();
            }
        });

        // Register the "start" walk button
        Button startWalkBtn = findViewById(R.id.start_existing_walk_btn);
        startWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalkActivity(route);
            }
        });


        TextView routeNameText = findViewById(R.id.route_detail_name);
        routeNameText.setText(route.getRouteName());

        TextView startingPointText = findViewById(R.id.starting_point_text_view);
        startingPointText.setText(route.getStartingPoint());

//        Date routeDate = route.getDate();
//        if (routeDate == null) {
//            // Convert LocalDateTime to Date
//            routeDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
//        }
        //TODO: Migrate from LocalDateTime
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//        String formattedDate = routeDate.format(formatter);
        TextView routeDateText = findViewById(R.id.route_detail_date);
        routeDateText.setText("DATE in progress");

        double miles = route.getMiles();
        TextView routeMilesText = findViewById(R.id.miles_text_view);
        routeMilesText.setText(String.valueOf(miles));

        long steps = route.getSteps();
        TextView routeStepsText = findViewById(R.id.steps_text_view);
        routeStepsText.setText(String.valueOf(steps));

        TextView noteText = findViewById(R.id.notes_text_view);
        noteText.setText(route.getNotes());

        List<String> tags = route.getTags();
        if (tags != null) {
            int i = 1;
            for (String tag : tags) {
                TextView tagText;
                switch (i) {
                    case 1:
                        tagText = findViewById(R.id.tag1);
                        tagText.setText(tag);
                        break;
                    case 2:
                        tagText = findViewById(R.id.tag2);
                        tagText.setText(tag);
                        break;
                    case 3:
                        tagText = findViewById(R.id.tag3);
                        tagText.setText(tag);
                        break;
                    case 4:
                        tagText = findViewById(R.id.tag4);
                        tagText.setText(tag);
                        break;
                    case 5:
                        tagText = findViewById(R.id.tag5);
                        tagText.setText(tag);
                        break;
                    default:
                        break;
                }
                i++;
            }
        }

        Context current = this;
        boolean isFavorite = route.isFavorite();
        mFavoriteBtn = findViewById(R.id.favoriteBtnDetail);
        if (isFavorite) {
            mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_on));
            mFavoriteBtn.setChecked(true);
        } else {
            mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_off));
        }
        mFavoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_on));
                } else
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_off));
            }
        });
    }

    /**
     * Starts the Walk activity from the RouteDetailActivity
     */
    private void startWalkActivity(Route route) {
        Intent intent = new Intent(RouteDetailActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_ROUTE_DETAIL_ACTIVITY_CALLER_ID);
        // Put a route extra so the Walk activity can display its title
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        startActivityForResult(intent, START_EXISTING_WALK_REQUEST_CODE);
    }

    /**
     * Returns updated Walk stats to the RoutesActivity
     */
    private void returnToRoutesActivity(Route route) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        // Return the Firestore info that was passed into this activity
        Intent incomingIntent = getIntent();

        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY));
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_ID_KEY));

        // Pass this Intent back
        setResult(Activity.RESULT_OK, returnIntent);
        // Go back to the Routes screen, bypassing the RouteDetail
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");
        // Check that this is the Walk Activity calling back
        if (requestCode == START_EXISTING_WALK_REQUEST_CODE) {
            // If the user stopped the walk normally and there were no errors
            if (resultCode == Activity.RESULT_OK) {
                // Get the new Walk
                Walk walk = (Walk) (data.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));

                // Update the corresponding Route with the new Walk
                Route route = (Route) (getIntent().getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

//                route.setDate(walk.getDate());
                route.setDuration(walk.getDuration());
                route.setSteps(walk.getSteps());
                route.setMiles(walk.getMiles());

                // Log.d(TAG, "Route object in RouteDetailActivity is\n" + route.toString());

                // Return data to the routes activity
                returnToRoutesActivity(route);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // If the walk ended abnormally, indicate this with a null Route
                returnToRoutesActivity(null);

            } else {
                Log.d(TAG, "Result code (unhandled) returned was: " + resultCode);
            }
        } else {
            Log.d(TAG, "Request code " + requestCode + " is unhandled");
        }
    }

}
