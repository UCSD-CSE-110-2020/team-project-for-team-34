package com.example.wwrapp.activities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.wwrapp.R;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Walk;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Detailed information page for a Route
 */
public class RouteDetailActivity extends AppCompatActivity {
    private static final String TAG = "RouteDetailActivity";
    private static final int START_EXISTING_WALK_REQUEST_CODE = 1;

    private ToggleButton mFavoriteBtn;

    private FirebaseFirestore mFirestore;
    private AbstractUser mUser;
    private Walk mWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mFirestore = FirebaseFirestore.getInstance();

        // Get this user
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

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

        // Set up the display
        TextView routeNameText = findViewById(R.id.route_detail_name);
        routeNameText.setText(route.getRouteName());

        TextView startingPointText = findViewById(R.id.starting_point_text_view);
        startingPointText.setText(route.getStartingPoint());

        TextView routeDateText = findViewById(R.id.route_detail_date);
        routeDateText.setText(route.getDateOfLastWalk());

        double miles = route.getMiles();
        TextView routeMilesText = findViewById(R.id.miles_text_view);
        routeMilesText.setText(String.valueOf(miles));

        long steps = route.getSteps();
        TextView routeStepsText = findViewById(R.id.steps_text_view);
        routeStepsText.setText(String.valueOf(steps));

        TextView noteText = findViewById(R.id.notes_text_view);
        noteText.setText(route.getNotes());

        // Display the tags
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

        Context currentContext = this;
        boolean isFavorite = route.isFavorite();
        mFavoriteBtn = findViewById(R.id.favoriteBtnDetail);
        if (isFavorite) {
            mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_on));
            mFavoriteBtn.setChecked(true);
        } else {
            mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_off));
        }

        // Nested field name for favoriters
        String nestedFieldName = RouteDocumentNameUtils.getNestedFieldName(Route.FIELD_FAVORITERS, mUser.getEmail());

        mFavoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Clicked the favorite button");
                String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mUser.getEmail(), route.getRouteName());


                if (isChecked) {
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_on));

                    // Update the route's favorite status on Firestore

                    // Update the user's personal route
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .update(Route.FIELD_FAVORITE, true,
                                    Route.FIELD_FAVORITERS, nestedFieldName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated route favorite in personal collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favorite to personal collection", e);
                                }
                            });

                    // Update the route favorite on the team screen, if the user is on a team
                    if (!mUser.getTeamName().isEmpty()) {
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .update(Route.FIELD_FAVORITE, true,
                                        nestedFieldName, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated route favorite in team collection");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing favorite to team collection", e);
                                    }
                                });
                    }


                } else {
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_off));

                    // Update the user's personal route
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .update(Route.FIELD_FAVORITE, false,
                                    nestedFieldName, false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated route favorite in personal collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favorite to personal collection", e);
                                }
                            });

                    // Update the route favorite on the team screen, if the user is on a team
                    if (!mUser.getTeamName().isEmpty()) {
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .update(Route.FIELD_FAVORITE, false,
                                        nestedFieldName, false)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated route favorite in team collection");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing favorite to team collection", e);
                                    }
                                });
                    }
                }
            } // end onCheckedChanged
        });
    } // end method

    /**
     * Starts the Walk activity from the RouteDetailActivity
     */
    private void startWalkActivity(Route route) {
        Intent intent = new Intent(RouteDetailActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_ROUTE_DETAIL_ACTIVITY_CALLER_ID);
        // Put a route extra so the Walk activity can display its title
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);

        startActivityForResult(intent, START_EXISTING_WALK_REQUEST_CODE);
    }

    /**
     * Returns updated Walk stats to the RoutesActivity
     */
    private void returnToRoutesActivity(Route route) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        returnIntent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, mWalk);

        // Return the Firestore info that was passed into this activity
        Intent incomingIntent = getIntent();
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY));
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_ID_KEY));

        // If the walk ended abnormally
        if (route == null) {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        } else {
            // if the walk ended normally
            setResult(Activity.RESULT_OK, returnIntent);
        }
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
                mWalk = (Walk) (data.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));

                // Update the corresponding Route with the new Walk
                Route route = (Route) (getIntent().getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

                route.setSteps(mWalk.getSteps());
                route.setMiles(mWalk.getMiles());
                route.setDateOfLastWalk(mWalk.getDate());

                // Update the walkers for the route
                route.putWalker(mUser.getEmail(), mWalk);

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
