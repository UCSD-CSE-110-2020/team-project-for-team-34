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
import com.google.firebase.firestore.CollectionReference;
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

        // Get the database
        mFirestore = FirebaseFirestore.getInstance();

        // For testing InviteMember
        if(testInvite){
            Intent intent = new Intent(TeamActivity.this, InviteMemberScreenActivity.class);
            startActivity(intent);
        }

        mUser = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));


        // get user and check if he/she has inviter
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
                        if (!newInviterName.isEmpty()) {
                            mUser.setInviterName(newInviterName);
                            mUser.setInviterEmail(newInviterEmail);
                            Intent intent = new Intent(TeamActivity.this, InviteMemberScreenActivity.class);
                            intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
                            startActivityForResult(intent, INVITE_ACTIVITY_REQUEST_CODE);
                        }
                    } else {

                        Log.d(TAG, "No such document for invitee");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Log.d(TAG, "inviter Email is " + mUser.getInviterEmail());





        // Set up Firestore and query for the routes to display
        initFirestore();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        mAddNewTeamBtn = findViewById(R.id.addNewTeamButton);
        mAddNewTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamActivity.this, AddTeamMemberActivity.class);
                final IUser user = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
                intent.putExtra(WWRConstants.EXTRA_USER_KEY, user);
                startActivity(intent);
//                intent.putExtra(WWRConstants.EXTRA_USER_TYPE_KEY, WWRConstants.MOCK_USER_FACTORY_KEY);
//                intent.putExtra("TEST", "TEST_DOC_NAME");
//                startActivityForResult(intent, ADD_TEAM_MEMBER_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    private void initFirestore() {


        CollectionReference teamCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH);
        
        if(mUser.getTeamName().equals(""))
        {
            Log.d(TAG, "user is not on a team");
            mQuery = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                    .document(mUser.getEmail()).collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH);

        }
        else
        {
            Log.d(TAG, "user is on team: " + mUser.getTeamName());
            mQuery = teamCol.document(mUser.getTeamName()).collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH);
            if(mQuery == null){
                Log.d(TAG, "initFirestore: empty query");
            }
        }

        initRecyclerView();

        // Check if the user belongs to a team
        /*teamCol.whereEqualTo(MockUser.FIELD_EMAIL, mUser.getEmail()).get()
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
                });*/

        // If the user belongs to a team, query for the other team members
        /*if (mUserIsOnTeam) {
            Log.d(TAG, "user is on a team");
            mQuery = teamCol.orderBy(MockUser.FIELD_NAME, Query.Direction.DESCENDING);
        } else {
            Log.d(TAG, "user is not on a team");
        }*/

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
            FirestoreRecyclerOptions<TeamMember> options =
                    new FirestoreRecyclerOptions.Builder<TeamMember>().setQuery(mQuery, TeamMember.class).build();
            mTeamAdapter = new TeamAdapter(options,mUser);

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

    public static void mockInviteMemberScreen(boolean testInviteMember){
        testInvite = testInviteMember;
    }


}
