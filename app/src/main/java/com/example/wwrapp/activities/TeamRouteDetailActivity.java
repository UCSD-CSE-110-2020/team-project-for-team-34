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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Detailed information page for a Route
 * TODO: Using RouteDetailActivity as a reference, correct this class
 */
public class TeamRouteDetailActivity extends AppCompatActivity {
    private static final String TAG = "TeamRouteDetailActivity";
    private static final int START_EXISTING_WALK_REQUEST_CODE = 1;

    private ToggleButton mFavoriteBtn;

    private AbstractUser mUser;
    private FirebaseFirestore mFirestore;
    private Route mRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammate_route_detail);

        mFirestore = FirebaseFirestore.getInstance();

        // Get the clicked on route
        mRoute = (Route) (getIntent().getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

        // Get the user
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

        mFavoriteBtn = findViewById(R.id.favoriteBtnDetail);

        // Register the "X"/close screen button
        findViewById(R.id.close_route_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close up this activity
                Log.d(TAG, "Clicked 'X' button");
                finish();
            }
        });

        // Register the "propose" walk button
        Button proposeWalkBtn = findViewById(R.id.propose_walk_btn);
        proposeWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : implement propose walk
            }
        });

        Button startWalkButton = findViewById(R.id.start_existing_walk_team_route_btn);
        startWalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalkActivity(mRoute);
            }
        });

        TextView routeNameText = findViewById(R.id.route_detail_name);
        routeNameText.setText(mRoute.getRouteName());

        TextView startingPointText = findViewById(R.id.starting_point_text_view);
        startingPointText.setText(mRoute.getStartingPoint());

        // TODO: Substitute route stats if applicable

        Context currentContext = this;
        TextView routeDateText = findViewById(R.id.route_detail_date);

        double miles = mRoute.getMiles();
        TextView routeMilesText = findViewById(R.id.miles_text_view);

        long steps = mRoute.getSteps();
        TextView routeStepsText = findViewById(R.id.steps_text_view);

        String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mRoute.getOwnerEmail(), mRoute.getRouteName());
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                .document(routeDocName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Log.d(TAG, "Got route data: " + document.getData());

                                // Check which data can be substituted

                                // If the user has their own rating, display that instead
                                Map<String, Boolean> favoriters = (Map<String, Boolean>) (document.get(Route.FIELD_FAVORITERS));
                                if (favoriters.containsKey(mUser.getEmail())) {
                                    boolean isFavorite = favoriters.get(mUser.getEmail());
                                    if (isFavorite) {
                                        mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_on));
                                        mFavoriteBtn.setChecked(true);
                                    } else {
                                        mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_off));
                                        mFavoriteBtn.setChecked(false);
                                    }

                                } else {
                                    // If the user doesn't have their own rating, substitute the owner's.
                                    Log.d(TAG, "Current user " + mUser.getEmail() + " has NOT favorited route ");

                                    boolean isOwnerFavorite = mRoute.isFavorite();
                                    if (isOwnerFavorite) {
                                        mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_on));
                                        mFavoriteBtn.setChecked(true);
                                    } else {
                                        mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_off));
                                        mFavoriteBtn.setChecked(false);
                                    }
                                }

                                // If the user has walked this route before, display their stats
                                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                        .document(routeDocName)
                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                        .document(mUser.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        // If user has walked before
                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        Walk walk = document.toObject(Walk.class);
                                                        routeMilesText.setText(String.valueOf(walk.getMiles()));
                                                        routeStepsText.setText(String.valueOf(walk.getSteps()));
                                                        routeDateText.setText(walk.getDate());

                                                        // TODO: Display a check mark if the user has walked this route
                                                        Log.d(TAG, "Current user " + mUser.getEmail() + " has walked route before");
                                                    } else {
                                                        // If user has not walked before
                                                        Log.d(TAG, "No such document");
                                                        // Substitute the owner's stats
                                                        routeMilesText.setText(String.valueOf(miles));
                                                        routeStepsText.setText(String.valueOf(steps));
                                                        routeDateText.setText(mRoute.getDateOfLastWalk());
                                                        Log.d(TAG, "Current user " + mUser.getEmail() + " has NOT walked route before");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });

                            } else {
                                Log.d(TAG, "Couldn't find Route");
                            }
                        } else {
                            Log.w(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        // TODO: Respond to changes
        mFavoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_on));

                    // Update favorite rating
                    mRoute.putFavoriter(mUser.getEmail(), true);
                    Map<String, Boolean> updatedFavoriters = mRoute.getFavoriters();

                    // Get the nested field to update
                    String updateField = RouteDocumentNameUtils.getNestedFieldName(Route.FIELD_FAVORITERS, mUser.getEmail());
                    Log.d(TAG, "Update field is " + updateField);

                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                            .document(routeDocName)
                            .update(updateField, true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated user as route favoriter in team collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favoriter to team collection", e);
                                }
                            });

                } else {
                    mFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(currentContext, R.drawable.ic_star_off));

                    // Get the nested field to update
                    String updateField = RouteDocumentNameUtils.getNestedFieldName(Route.FIELD_FAVORITERS, mUser.getEmail());
                    Log.d(TAG, "Update field is " + updateField);

                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                            .document(routeDocName)
                            .update(updateField, false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated user as route un-favoriter in team collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favoriter to team collection", e);
                                }
                            });
                }

            }
        });


        TextView noteText = findViewById(R.id.notes_text_view);
        noteText.setText(mRoute.getNotes());

        TextView teammateText = findViewById(R.id.teammateName);
        teammateText.setText(mRoute.getOwnerName());

        List<String> tags = mRoute.getTags();
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


    }

    /**
     * Starts the Walk activity from the RouteDetailActivity
     */
    private void startWalkActivity(Route route) {
        Intent intent = new Intent(TeamRouteDetailActivity.this, WalkActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY,
                WWRConstants.EXTRA_TEAM_ROUTE_DETAIL_ACTIVITY_CALLER_ID);
        // Put a route extra so the Walk activity can display its title
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
        startActivityForResult(intent, START_EXISTING_WALK_REQUEST_CODE);
    }

    /**
     * Returns updated Walk stats to the RoutesActivity
     */
    private void returnToTeamRoutesActivity(Walk walk) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY, walk);
        // Return the Firestore info that was passed into this activity
        Intent incomingIntent = getIntent();

        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, mRoute);
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY));
        returnIntent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, incomingIntent.getStringExtra(WWRConstants.EXTRA_ROUTE_ID_KEY));

        // Pass this Intent back
        if (walk == null) {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        } else {
            setResult(Activity.RESULT_OK, returnIntent);
        }
        // Go back to the Routes screen
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

                // Return data to the routes activity
                returnToTeamRoutesActivity(walk);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // If the walk ended abnormally, indicate this with a null Walk
                returnToTeamRoutesActivity(null);

            } else {
                Log.d(TAG, "Result code (unhandled) returned was: " + resultCode);
            }
        } else {
            Log.d(TAG, "Request code " + requestCode + " is unhandled");
        }
    }

}
