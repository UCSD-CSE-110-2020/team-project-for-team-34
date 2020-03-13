package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.R;
import com.example.wwrapp.adapters.RouteAdapter;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Walk;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class RoutesActivity extends AppCompatActivity implements RouteAdapter.OnRouteSelectedListener {

    private static final String TAG = "RoutesActivity";
    private static final int START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;
    private static final int START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE = 2;

    private static final int QUERY_LIMIT = 50;
    private static boolean disablemUser = false;

    // UI-related objects
    private RouteAdapter mRouteAdapter;
    private RecyclerView mRoutesRecycler;
    private Button mAddNewRouteButton;
    private Button mTeammateRouteButton;

    // Backend-related objects
    private AbstractUser mUser;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private static boolean sIsTest = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "in onCreate");


        if (disablemUser) {
            findViewById(R.id.teammateRouteBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RoutesActivity.this, TeamRoutesActivity.class);
                    startActivity(intent);
                }
            });
            findViewById(R.id.addNewRouteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RoutesActivity.this, EnterWalkInformationActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // Get this user
            mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

            // Set up Firestore and query for the routes to display
            initFirestore();

            // Set up recycler view for routes
            initRecyclerView();

            // Enable Firestore logging
            FirebaseFirestore.setLoggingEnabled(true);

            // Testing code block
            if (sIsTest) {
                generateFakeRoute();
            }

            // Register the add new route button
            mAddNewRouteButton = findViewById(R.id.addNewRouteButton);
            mAddNewRouteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAddNewRouteActivity();
                }
            });

            // Register the teammate route button
            mTeammateRouteButton = findViewById(R.id.teammateRouteBtn);
            mTeammateRouteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RoutesActivity.this, TeamRoutesActivity.class);
                    intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
                    startActivity(intent);
                }
            });


            // Determine who launched the RoutesActivity
            Intent intent = getIntent();
            String callerId = intent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
            Log.i(TAG, "callerID is: " + callerId);

            // If the Routes activity was launched by the EnterWalkInformation activity, then
            // update the routes database.
            switch (callerId) {
                case WWRConstants.EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID:
                    Intent incomingIntent = getIntent();
                    Walk walk = (Walk) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));
                    Route route = (Route) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                    Log.d(TAG, route.toString());

                    boolean manuallyCreatedRoute = incomingIntent.getBooleanExtra
                            (WWRConstants.EXTRA_MANUALLY_CREATED_ROUTE_KEY, false);

                    // If the route was generated by a spontaneous walk
                    if (!manuallyCreatedRoute) {
                        // Save the newest walk
                        SharedPreferences sharedPreferences =
                                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, route.getSteps());
                        editor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) route.getMiles());
                        editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, route.getDateOfLastWalk());
                        editor.apply();
                    }


                    // Save the newest walk
                    SharedPreferences sharedPreferences =
                            getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, route.getSteps());
                    editor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) route.getMiles());
                    editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, route.getDateOfLastWalk());

                    editor.apply();
                    Log.d(TAG, "Right before route insertion");

                    // Add the route to the user's personal routes.
                    String routeDocName = RouteDocumentNameUtils.getRouteDocumentName
                            (mUser.getName(), route.getRouteName());
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .set(route)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully added route " + routeDocName + " for user!");

                                    // Add this user as a walker
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                                            .document(mUser.getEmail())
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                            .document(mUser.getEmail())
                                            .set(walk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Successfully added user as walker!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing walker", e);
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing route", e);
                                }
                            });

                    // If the user is on a team, also add the route to the team
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                            .document(routeDocName)
                            .set(route)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully added route " + routeDocName + " for team!");

                                    // Add the user as a walker
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                            .document(mUser.getEmail())
                                            .set(walk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Successfully added user as walker to team!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing walker", e);
                                                }
                                            });

                                    // Add the user as a favoriter
                                    Map<String, Boolean> map = new HashMap<>();
                                    map.put(mUser.getEmail(), route.isFavorite());
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_FAVORITERS_PATH)
                                            .document(mUser.getEmail())
                                            .set(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Successfully added user as favoriter!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing walker", e);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing route", e);
                                }
                            });

                    break;
                case WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID:
                    break;
                default:
                    Log.d(TAG, "caller is: " + callerId);
            }

        }

    }

    /**
     * Sets up Firestore and queries for the routes to display
     */
    private void initFirestore() {
        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        // Query for the user's routes
        mQuery = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail())
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                .orderBy(Route.FIELD_NAME, Query.Direction.ASCENDING);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        } else {
            // Set up adapter
            FirestoreRecyclerOptions<Route> options =
                    new FirestoreRecyclerOptions.Builder<Route>().setQuery(mQuery, Route.class).build();
            mRouteAdapter = new RouteAdapter(options, mUser);

            // Set up recycler view
            mRoutesRecycler = findViewById(R.id.recycler_view_route);
            mRoutesRecycler.setHasFixedSize(true);
            mRoutesRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRoutesRecycler.setAdapter(mRouteAdapter);

            // Make this activity listen for changes to the adapter
            mRouteAdapter.setOnRouteSelectedListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "in onStart");
        if (mRouteAdapter != null) {
            mRouteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        if (mRouteAdapter != null) {
            mRouteAdapter.stopListening();
        }
    }

    /**
     * Starts the RouteDetailActivity
     *
     * @param route: the Route to start the detail activity for
     */
    private void startRouteDetailActivity(Route route) {
        Intent intent = new Intent(RoutesActivity.this, RouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    private void startAddNewRouteActivity() {
        Log.d(TAG, "Starting Add new route activity");
        Intent intent = new Intent(RoutesActivity.this, EnterWalkInformationActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_ROUTES_ACTIVITY_CALLER_ID);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
        startActivityForResult(intent, START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");
        Log.d(TAG, "Request code is: " + requestCode);
        Log.d(TAG, "Result code is: " + resultCode);


        switch (requestCode) {
            case START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE:
                Log.d(TAG, "Returned from walk existing route");

                // If the RouteDetailActivity finished normally
                if (resultCode == Activity.RESULT_OK) {
                    // Get the walk object
                    Walk walk = (Walk) (data.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));
                    // Get the updated Route object
                    Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                    assert route != null;

                    Log.d(TAG, route.toString());

                    // Get the name of the route document so we can update it
                    String path = data.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY);
                    String routeDocName = RouteDocumentNameUtils.getRouteDocumentNameFromPath(path);
                    Log.d(TAG, "Name of route doc is " + routeDocName);

                    // Perform the update
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName).update(
                            Route.FIELD_STEPS, route.getSteps(),
                            Route.FIELD_MILES, route.getMiles(),
                            Route.FIELD_DATE, route.getDateOfLastWalk(),
                            Route.FIELD_WALKED, route.isWalked())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated existing route!");

                                    // Update walkers
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                                            .document(mUser.getEmail())
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                            .document(mUser.getEmail())
                                            .set(walk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Successfully added user as walker!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing walker", e);
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating route", e);
                                }
                            });

                    // If the user is on a team, also add the route to the team
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                            .document(routeDocName)
                            .update(
                                    Route.FIELD_STEPS, route.getSteps(),
                                    Route.FIELD_MILES, route.getMiles(),
                                    Route.FIELD_DATE, route.getDateOfLastWalk()
                            )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully updated route " + routeDocName + " for team!");

                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                            .document(mUser.getEmail())
                                            .set(walk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Successfully added user as walker to team!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing walker", e);
                                                }
                                            });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing route", e);
                                }
                            });


                } else {
                    // If the RouteDetailActivity did not finish normally, do nothing
                    Log.d(TAG, "RouteDetailActivity finished abnormally with result code " + resultCode);
                }
                break;
            case START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Add the new route
                    Log.d(TAG, "Returned from AddNewRouteActivity");
                    Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                    assert route != null;

                    // Add the route based on the user's identity.
                    String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mUser.getEmail(), route.getRouteName());
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .set(route).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully added route " + routeDocName + " for user!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing route", e);
                                }
                            });

                    // If the user is on a team, also add the route to the team
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                            .document(routeDocName)
                            .set(route)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully added route " + routeDocName + " for team!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing route", e);
                                }
                            });

                } else {
                    Log.d(TAG, "Activity result ( " + resultCode + ") not OK from add new route");
                }
                break;
        } // end switch
    }

    public void generateFakeRoute() {
        Route testRoute = new Route();
//        mRouteViewModel.insert(testRoute);
    }

    public static void setIsTest(boolean isTest) {
        RoutesActivity.sIsTest = isTest;
    }

    @Override
    public void onRouteSelected(DocumentSnapshot documentSnapshot, int position) {
        Route route = documentSnapshot.toObject(Route.class);
        String routeId = documentSnapshot.getId();
        String path = documentSnapshot.getReference().getPath();
        Log.d(TAG, "Route id is " + route);
        Log.d(TAG, "Path is " + path);

        // When a route is tapped, launch its detail page:
        Intent intent = new Intent(RoutesActivity.this, RouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, routeId);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, path);

        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    public static void disableUser(Boolean disable) {
        disablemUser = disable;
    }

}
