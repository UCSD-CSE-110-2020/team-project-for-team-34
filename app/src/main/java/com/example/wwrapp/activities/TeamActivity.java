package com.example.wwrapp.activities;

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

import com.example.wwrapp.R;
import com.example.wwrapp.adapters.TeamAdapter;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.MockUser;
import com.example.wwrapp.models.TeamMember;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeamActivity extends AppCompatActivity {
    private static final String TAG = "TeamActivity";

    private static final int ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE = 1;
    private static final int INVITE_ACTIVITY_REQUEST_CODE = 2;


    private TeamAdapter mTeamAdapter;
    private RecyclerView mTeamRecycler;
    private Button mAddNewTeamBtn;

    // Backend-related objects
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private IUser mUser;
    private boolean mUserIsOnTeam;

    // For testing InviteMemberScreen
    private static boolean testInvite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Log.d(TAG, "onCreate");

        // Render + button
        mAddNewTeamBtn = findViewById(R.id.addNewTeamButton);
        mAddNewTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamActivity.this, AddTeamMemberActivity.class);
                final IUser user = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
                intent.putExtra(WWRConstants.EXTRA_USER_KEY, user);
                startActivityForResult(intent, ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE);
            }
        });

        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Remove this if-block For testing InviteMember
        if (testInvite) {
            Intent intent = new Intent(TeamActivity.this, InviteMemberScreenActivity.class);
            startActivity(intent);
        }

        // Get this user
        mUser = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        Log.d(TAG, "inviter Email is " + mUser.getInviterEmail());

        // Check if user received a team invite since the Home Screen
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data for invitee: " + document.getData());
                        String newInviterName = document.getString(MockUser.FIELD_INVITER_NAME);
                        String newInviterEmail = document.getString(MockUser.FIELD_INVITER_EMAIL);
                        // If there a new inviter sent an invitation:
                        if (!newInviterName.isEmpty()) {
                            mUser.setInviterName(newInviterName);
                            mUser.setInviterEmail(newInviterEmail);
                            Intent intent = new Intent(TeamActivity.this, InviteMemberScreenActivity.class);
                            intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
                            startActivityForResult(intent, INVITE_ACTIVITY_REQUEST_CODE);
                        }
                    } else {
                        Log.d(TAG, "Invitee doesn't have any new invites");
                    }
                    // Set up Firestore and query for the routes to display
                    initFirestore();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Important! Listen for the data to display it.
        if (mTeamAdapter != null) {
            mTeamAdapter.startListening();
        }
        Log.d(TAG, "in onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        // Important! Stop listening for the data to not receive any updates and waste resources
        if (mTeamAdapter != null) {
            mTeamAdapter.stopListening();
        }
    }

    /**
     * Queries Firestore for the users to display
     */
    private void initFirestore() {
        Log.d(TAG, "initFirestore:");
        // If the user is not on a team, display "team members" from the invitees field
        if (mUser.getTeamName().isEmpty()) {
            Log.d(TAG, "user is not on a team");
            mQuery = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                    .document(mUser.getEmail()).collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH);

        } else {
            // If the user is on a team, display "team members" from the team members documents
            Log.d(TAG, "user is on team: " + mUser.getTeamName());
            mQuery = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                    .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                    .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH);
        }
        initRecyclerView();
    }

    /**
     * Sets up the list view of users
     */
    private void initRecyclerView() {
        Log.d(TAG, "in method init recyclerview");

        // If the query is null, meaning the user is not on a team, don't display anything
        if (mQuery == null) {
            Log.w(TAG, "No query!!!");
        } else {
            Log.d(TAG, "Query is not null");
            // Set up adapter
            FirestoreRecyclerOptions<TeamMember> options =
                    new FirestoreRecyclerOptions.Builder<TeamMember>()
                            .setQuery(mQuery, TeamMember.class)
                            .build();
            mTeamAdapter = new TeamAdapter(options, mUser);

            // Set up recycler view
            mTeamRecycler = findViewById(R.id.recycler_view_team);
            mTeamRecycler.setHasFixedSize(true);
            mTeamRecycler.setLayoutManager(new LinearLayoutManager(this));
            mTeamRecycler.setAdapter(mTeamAdapter);
            mTeamAdapter.startListening();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        if (requestCode == ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Returned from adding team member");
            // If a team member was added
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Result code OK");
            } else {
                Log.d(TAG, "Result code not OK");
            }
        } else if (requestCode == INVITE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Returned from invite activity");
        }
    }

    public static void mockInviteMemberScreen(boolean testInviteMember) {
        testInvite = testInviteMember;
    }


}
