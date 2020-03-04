package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.adapters.TeamAdapter;
import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.MockUser;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class TeamActivity extends AppCompatActivity {
    private static final String TAG = "TeamActivity";

    private static final int ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE = 1;

    private TeamAdapter mTeamAdapter;
    private RecyclerView mTeamRecycler;
    private Button mAddNewTeamBtn;

    // Backend-related objects
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private boolean mUserIsOnTeam;

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
                Intent intent = new Intent(TeamActivity.this, AddTeamMemberActivity.class);
                final IUser user = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
                intent.putExtra(WWRConstants.EXTRA_USER_KEY, user);
                intent.putExtra(WWRConstants.EXTRA_USER_TYPE_KEY, WWRConstants.MOCK_USER_FACTORY_KEY);
                intent.putExtra("TEST", "TEST_DOC_NAME");
                startActivityForResult(intent, ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    private void initFirestore() {
        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        // Get this app's user
        final IUser user = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

        CollectionReference teamCol = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH);

        // Check if the user belongs to a team
        teamCol.whereEqualTo(MockUser.FIELD_EMAIL, user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Query for user membership successful!");
                            // If the query returned anything, the user is on a team.
                            if (task.getResult().size() > 0) {
                                // TODO: Implement logic to gray out users that are pending
                                mUserIsOnTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // If the user belongs to a team, query for the other team members
        if (mUserIsOnTeam) {
            Log.d(TAG, "user is on a team");
            mQuery = teamCol.orderBy(MockUser.FIELD_NAME, Query.Direction.DESCENDING);
        } else {
            Log.d(TAG, "user is not on a team");
        }

        //TODO: double check the path of the user: resolved for now
//        Query query = teamCol.whereEqualTo("teamID","teamID")
//                .whereGreaterThan("userId","currentUserId")
//                .whereLessThan("userId","currentUserId");
        // Query for the user's routes
        //TODO:implement actual user model: resolved for now
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        // If the query is null, meaning the user is not on a team, don't display anything
        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        } else {
            // Set up adapter
            //TODO:set up actual user model: resolved for now
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Returned from adding team member");
            // If a team member was added
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Result code OK");
            } else {
                Log.d(TAG, "Result code not OK");
            }
        } else {
            Log.d(TAG, "Request code not recognized: " + requestCode);
        }
    }


}
