package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.R;
import com.example.wwrapp.adapters.TeamRouteAdapter;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Walk;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TeamRoutesActivity extends AppCompatActivity implements TeamRouteAdapter.OnRouteSelectedListener {
    private static final String TAG = "TeamRoutesActivity";
    private static final int START_TEAM_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;
    public static boolean IS_TESTING_EMPTY = false;

    private TeamRouteAdapter mTeammateRouteAdapter;
    private RecyclerView mTeammateRoutesRecycler;
    private TextView mEmptyStringView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private AbstractUser mUser;
    private static boolean mEmpty;
    private static boolean disablemUser =false;

    // For testing purposes
    private static boolean testTeammateRoute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammate_route);

        Log.d(TAG, "in onCreate");

        if(disablemUser){
            finish();
        }

        // Get the user
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

        if (IS_TESTING_EMPTY) {
            mEmpty = true;
            return;
        }
        // Set up Firestore and query for the routes to display
        initFirestore();

        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "in onStart");
        if (mTeammateRouteAdapter != null) {
            mTeammateRouteAdapter.startListening();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        if (mTeammateRouteAdapter != null) {
            mTeammateRouteAdapter.stopListening();
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        if (testTeammateRoute) {
            CollectionReference teamRouteCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH);
            Route testRoute = new Route();
            teamRouteCol.document("ellen@gmail.com").set(testRoute);
        }

        if (!mUser.getTeamName().isEmpty()) {
            Log.d(TAG, "initFirestore: " + mUser.getEmail());
            mQuery = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                    .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                    .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                    .orderBy(Route.FIELD_NAME, Query.Direction.ASCENDING);
            initRecyclerView();
        } else {
            Log.d(TAG, "User doesn't have a team.");
        }
    }

    public static boolean isEmpty() {
        return mEmpty;
    }

    // TODO: Why is this here?
    private void checkIfEmpty() {
        if (mQuery == null) {
            Log.d(TAG, "checkIfEmpty: Query is empty");
            mEmpty = true;
        } else {
            mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        boolean isEmpty = true;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            isEmpty = false;
                        }
                        mEmpty = isEmpty;
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void hideOrShowEmptyString() {
        mEmptyStringView = findViewById(R.id.emptyStringView);

        if (mEmpty) {
            mEmptyStringView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStringView.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        checkIfEmpty();
        hideOrShowEmptyString();
        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        } else {
            // Set up adapter
            FirestoreRecyclerOptions<Route> options =
                    new FirestoreRecyclerOptions.Builder<Route>().setQuery(mQuery, Route.class).build();
            mTeammateRouteAdapter = new TeamRouteAdapter(options, mUser);

            // Set up recycler view
            mTeammateRoutesRecycler = findViewById(R.id.recycler_teammate_view_route);
            mTeammateRoutesRecycler.setHasFixedSize(true);
            mTeammateRoutesRecycler.setLayoutManager(new LinearLayoutManager(this));
            mTeammateRoutesRecycler.setAdapter(mTeammateRouteAdapter);

            // Make this activity listen for changes to the adapter
            mTeammateRouteAdapter.setOnRouteSelectedListener(this);
        }
    }

    @Override
    public void onRouteSelected(DocumentSnapshot documentSnapshot, int position) {
        Route route = documentSnapshot.toObject(Route.class);
        String routeId = documentSnapshot.getId();
        String path = documentSnapshot.getReference().getPath();
        Log.d(TAG, "Route id is " + route);
        Log.d(TAG, "Path is " + path);

        // When a route is tapped, launch its detail page:
        Intent intent = new Intent(TeamRoutesActivity.this, TeamRouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, routeId);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, path);
        intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);

        startActivityForResult(intent, START_TEAM_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");
        Log.d(TAG, "Request code is: " + requestCode);
        Log.d(TAG, "Result code is: " + resultCode);

        if (requestCode == START_TEAM_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the new walk object
                Walk walk = (Walk) (data.getSerializableExtra(WWRConstants.EXTRA_WALK_OBJECT_KEY));

                String path = data.getStringExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY);
                String routeDocName = RouteDocumentNameUtils.getRouteDocumentNameFromPath(path);
                Log.d(TAG, "Route doc name is " + routeDocName);

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
                                Log.d(TAG, "Successfully updated existing walk stats!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating walk stats", e);
                            }
                        });
            } else {
                Log.w(TAG, "Result code is not handled");
            }
        } else {
            Log.w(TAG, "Request code is not handled");
        }


    }

    public static void setTestTeammateRoute(boolean testTeamRoute) {
        testTeammateRoute = testTeamRoute;
    }
    public static void disableUser(boolean disable){
        disablemUser = disable;
    }
}
