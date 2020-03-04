package com.example.wwrapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.adapters.RouteAdapter;
import com.example.wwrapp.adapters.TeamAdapter;
import com.example.wwrapp.model.MockUser;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeamActivity extends AppCompatActivity {
    private static final String TAG = "TeamActivity";

    private TeamAdapter mTeamAdapter;
    private RecyclerView mTeamRecycler;
    private Button mAddNewTeamBtn;

    // Backend-related objects
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        // Set up Firestore and query for the routes to display
        initFirestore();

        // Set up recycler view for routes
        initRecyclerView();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        mAddNewTeamBtn = findViewById(R.id.addNewTeamButton);
        mAddNewTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Implement add new teammate
            }
        });

    }

    private void initFirestore() {
        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        CollectionReference teamCol = mFirestore.collection("team");
        //TODO: double check the path of the user
        Query query = teamCol.whereEqualTo("teamID","teamID")
                .whereGreaterThan("userId","currentUserId")
                .whereLessThan("userId","currentUserId");
        // Query for the user's routes
        //TODO:implement actual user model
        mQuery = query.orderBy(MockUser.FIELD_NAME, Query.Direction.DESCENDING);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        }

        // Set up adapter
        //TODO:set up actual user model
        FirestoreRecyclerOptions<MockUser> options =
                new FirestoreRecyclerOptions.Builder<MockUser>().setQuery(mQuery, MockUser.class).build();
        mTeamAdapter = new TeamAdapter(options);

        // Set up recycler view
        mTeamRecycler = findViewById(R.id.recycler_view_team);
        mTeamRecycler.setHasFixedSize(true);
        mTeamRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTeamRecycler.setAdapter(mTeamAdapter);
    }
}
