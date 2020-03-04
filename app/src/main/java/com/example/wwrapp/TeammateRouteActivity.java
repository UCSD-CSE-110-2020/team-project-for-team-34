package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wwrapp.adapters.RouteAdapter;
import com.example.wwrapp.adapters.TeammateRouteAdapter;
import com.example.wwrapp.model.Route;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeammateRouteActivity extends AppCompatActivity implements TeammateRouteAdapter.OnRouteSelectedListener {
    private static final String TAG = "TeammateRoutesActivity";
    private static final int START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;

    private TeammateRouteAdapter mTeammateRouteAdapter;
    private RecyclerView mTeammateRoutesRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

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
        mTeammateRouteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        mTeammateRouteAdapter.stopListening();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        Query teamRoutes = mFirestore.collection("route").whereEqualTo("team","teamid")
               .whereLessThan("Owner","userId")
               .whereGreaterThan("Owner","userId");

        mQuery = teamRoutes.orderBy(Route.FIELD_NAME, Query.Direction.DESCENDING);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        }

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

    @Override
    public void onRouteSelected(DocumentSnapshot documentSnapshot, int position) {
        Route route = documentSnapshot.toObject(Route.class);
        String routeId = documentSnapshot.getId();
        String path = documentSnapshot.getReference().getPath();
        Log.d(TAG, "Route id is " + route);
        Log.d(TAG, "Path is " + path);

        // When a route is tapped, launch its detail page:
        Intent intent = new Intent(TeammateRouteActivity.this, RouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_ID_KEY, routeId);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_PATH_KEY, path);

        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }
}
