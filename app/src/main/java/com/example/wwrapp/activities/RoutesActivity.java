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
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.example.wwrapp.adapters.RouteAdapter;
import com.example.wwrapp.models.Route;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDateTime;

public class RoutesActivity extends AppCompatActivity implements RouteAdapter.OnRouteSelectedListener {

    private static final String TAG = "RoutesActivity";
    private static final int START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;
    private static final int START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE = 2;

    private static final int QUERY_LIMIT = 50;

    // UI-related objects
    private RouteAdapter mRouteAdapter;
    private RecyclerView mRoutesRecycler;
    private Button mAddNewRouteButton;
    private Button mTeammateRouteButton;

    // Backend-related objects
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private static boolean sIsTest = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "in onCreate");

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

        mTeammateRouteButton = findViewById(R.id.teammateRouteBtn);
        mTeammateRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutesActivity.this, TeammateRouteActivity.class);
                intent.putExtra(WWRConstants.EXTRA_USER_KEY,getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
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
                Route route = (Route) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

//                if (route.getDate() == null) {
//                    Log.e(TAG, "LocalDateTime is null");
//                }

                // Log.d(TAG, route.toString());

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
                    editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, route.getDuration());
                    editor.apply();
                }


                // Save the newest walk
                SharedPreferences sharedPreferences =
                        getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, route.getSteps());
                editor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) route.getMiles());

                // TODO: Remove upon migration from LocalDateTime
//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN_DETAILED);
//                String formattedDate = route.getDate().format(dateTimeFormatter);
//                Date date = route.getDate();
                LocalDateTime date = LocalDateTime.now();
                editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, date.toString());

                editor.apply();
                Log.d(TAG, "Right before route insertion");
                Log.d(TAG, route.toString());

                // TODO: Update the route based on the user's identity.
                // TODO: Currently this code assumes the presence of a dummy user
                // Traverse the data hierarchy
                CollectionReference usersCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH);
                DocumentReference userDoc = usersCol.document(FirestoreConstants.FIRESTORE_DOCUMENT_DUMMY_USER_PATH);
                CollectionReference myRoutesCol = userDoc.collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH);

                // Add the route to the user's routes
                myRoutesCol.add(route).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                break;
            case WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID:
                break;
            default:
                Log.d(TAG, "caller is: " + callerId);
        }


    }

    /**
     * Sets up Firestore and queries for the routes to display
     */
    private void initFirestore() {
        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Query based on the user's ID
        // TODO: The current code uses a dummy user.
        // Traverse the data hierarchy to get the user's routes
        CollectionReference usersCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH);
        DocumentReference userDoc = usersCol.document(FirestoreConstants.FIRESTORE_DOCUMENT_DUMMY_USER_PATH);
        CollectionReference myRoutes = userDoc.collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH);

        // Query for the user's routes
        mQuery = myRoutes.orderBy(Route.FIELD_NAME, Query.Direction.DESCENDING);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        }

        // Set up adapter
        FirestoreRecyclerOptions<Route> options =
                new FirestoreRecyclerOptions.Builder<Route>().setQuery(mQuery, Route.class).build();
        mRouteAdapter = new RouteAdapter(options);

        // Set up recycler view
        mRoutesRecycler = findViewById(R.id.recycler_view_route);
        mRoutesRecycler.setHasFixedSize(true);
        mRoutesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRoutesRecycler.setAdapter(mRouteAdapter);

        // Make this activity listen for changes to the adapter
        mRouteAdapter.setOnRouteSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "in onStart");
        mRouteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        mRouteAdapter.stopListening();
    }

    /**
     * Starts the RouteDetailActivity
     *
     * @param route: the Route to start the detail activity for
     */
    private void startRouteDetailActivity(Route route) {
        Intent intent = new Intent(RoutesActivity.this, RouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    private void startAddNewRouteActivity() {
        Log.d(TAG, "Starting Add new route activity");
        Intent intent = new Intent(RoutesActivity.this, EnterWalkInformationActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_ROUTES_ACTIVITY_CALLER_ID);
        startActivityForResult(intent, START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");
        Log.d(TAG, "Request code is: " + requestCode);
        Log.d(TAG, "Result code is: " + resultCode);

        // Check that this is the RouteDetailActivity calling back
        if (requestCode == START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Returned from walk existing route");

            // If the RouteDetailActivity finished normally
            if (resultCode == Activity.RESULT_OK) {
                // Get the potentially updated Route object
                Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                // If the route object is null, then no updates should be made
                if (route == null) {
                    Log.d(TAG, "Route object returned in onActivityResult is null");
                } else {
                    // TODO: Update a re-walk and find user based on email/key
                    // Otherwise, update the appropriate route object
                    Log.d(TAG, route.toString());

                    // Traverse the data hierarchy
                    CollectionReference userRef = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH);
                    DocumentReference userDoc = userRef.document(FirestoreConstants.FIRESTORE_DOCUMENT_DUMMY_USER_PATH);
                    CollectionReference myRoutesCol = userDoc.collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH);

                    // TODO: Find a simpler way to get the reference to the document to be updated
                    String path = data.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY);
                    int indexOfLastSlash = path.lastIndexOf("/");
                    String docName = path.substring(indexOfLastSlash + 1);
                    Log.d(TAG, "Received doc key is " + docName);

                    // Perform the update
                    myRoutesCol.document(docName).update(
                            Route.FIELD_STEPS, route.getSteps(),
                            Route.FIELD_MILES, route.getMiles()
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            } else {
                // If the RouteDetailActivity did not finish normally, do nothing
                Log.d(TAG, "RouteDetailActivity finished abnormally");
            }
        } else if (requestCode == START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Add the new route
                Log.d(TAG, "Returned from add new route");
                Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                // Log.d(TAG, route.toString());

                // TODO: Get the document corresponding to this user and add the Route.
                // TODO: Currently, there is only a dummy user
                // Traverse the data hierarchy
                CollectionReference userCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH);
                DocumentReference userDoc = userCol.document(FirestoreConstants.FIRESTORE_DOCUMENT_DUMMY_USER_PATH);
                CollectionReference myRoutesCol = userDoc.collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH);

                // Add the route to the collection as a document
                // TODO: Make sure that auto-generated Route ID's make sense.
                myRoutesCol.add(route).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            } else {
                Log.d(TAG, "Activity result not OK from add new route");
            }

        } else {
            Log.d(TAG, "Result of RouteDetailActivity is (not OK): " + resultCode);
        }
    }

    public void generateFakeRoute() {
        Route testRoute = new Route("route", "staring", "", 10, 10, null, true, "");
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

        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

}
