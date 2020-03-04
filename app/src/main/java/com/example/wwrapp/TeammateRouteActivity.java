package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.adapters.TeammateRouteAdapter;
import com.example.wwrapp.model.Route;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class TeammateRouteActivity extends AppCompatActivity implements TeammateRouteAdapter.OnRouteSelectedListener {
    private static final String TAG = "TeammateRoutesActivity";
    private static final int START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;

    private TeammateRouteAdapter mTeammateRouteAdapter;
    private RecyclerView mTeammateRoutesRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private boolean mUserBelongsToTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammate_route);

        Log.d(TAG, "in onCreate");

        // Set up Firestore and query for the routes to display
        initFirestore();

        // Set up recycler view for routes
        initRecyclerView();

        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "in onStart");
        // TODO: Remove null check once database fully functions
        if (mTeammateRouteAdapter != null) {
            mTeammateRouteAdapter.startListening();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        // TODO: Remove null check once database fully functions
        if (mTeammateRouteAdapter != null) {
            mTeammateRouteAdapter.stopListening();
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Check if this user belongs to a team
        // TODO: Check the "invitations" collection and ensure that the user is not a pending invitee
        // TODO: Or check the user's invite status
        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                mUserBelongsToTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // TODO: Query only for the routes that don't belong to this user
        if (mUserBelongsToTeam) {
            mQuery = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMMATE_ROUTES_PATH).orderBy(Route.FIELD_NAME, Query.Direction.DESCENDING);
        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        } else {
            // Set up adapter
            FirestoreRecyclerOptions<Route> options =
                    new FirestoreRecyclerOptions.Builder<Route>().setQuery(mQuery, Route.class).build();
            mTeammateRouteAdapter = new TeammateRouteAdapter(options);

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
        Intent intent = new Intent(TeammateRouteActivity.this, TeammateRouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, routeId);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, path);

        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }
}
