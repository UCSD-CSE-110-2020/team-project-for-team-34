package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.MockUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * TODO: Check that users are added to the database correctly.
 */
public class AddTeamMemberActivity extends AppCompatActivity {
    private static final String TAG = "AddTeamMemberActivity";

    private Button mConfirmBtn;
    private Button mCancelBtn;
    private EditText mNewMemberName;
    private EditText mNewMemberEmail;
    private String mMemberName;
    private String mMemberEmail;

    private FirebaseFirestore mFirestore;
    private boolean mInviterIsOnTeam;
    private boolean mInviteeIsOnTeam;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Get database instance
        mFirestore = FirebaseFirestore.getInstance();

        mNewMemberName = (EditText) findViewById(R.id.member_name_edit_text);
        mNewMemberEmail = (EditText) findViewById(R.id.member_email_edit_text);
        mMemberName = mNewMemberName.getText().toString();
        mMemberEmail = mNewMemberEmail.getText().toString();
        mConfirmBtn = (Button) findViewById(R.id.add_member_button);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save member_name and member_email to database and go to team screen.
                Intent intent = getIntent();
                IUser inviter = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

                // Check if the inviter is already in a team
                setInviterIsOnTeam(inviter, mFirestore);

                // Add the inviter to the team, if they don't belong to the team already
                if (!mInviterIsOnTeam) {
                    IUser userToAdd = new MockUser(inviter.getName(), inviter.getEmail(), WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);
                    addUserToTeam(userToAdd, mFirestore);
                }

                IUser invitee = new MockUser(mMemberName, mMemberEmail, WWRConstants.FIRESTORE_TEAM_INVITE_PENDING);
                // Check if the inviter is already invited (i.e. already in a team)
                setInviteeIsOnTeam(invitee, mFirestore);
                // Add the invitee to the team, if they don't belong to the team already
                if (!mInviteeIsOnTeam) {
                    addUserToTeam(invitee, mFirestore);
                }

                // Go to the Team screen
                finish();
            }
        });

        mCancelBtn = (Button) findViewById(R.id.add_member_cancel_button);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to previous screen
                finish();
            }
        });
    }

    /**
     * Sets the inviter's invite status to true if this user exists on a team, false otherwise
     *
     * @param user
     * @param firestore
     */
    private void setInviterIsOnTeam(IUser user, FirebaseFirestore firestore) {
        CollectionReference teamsCol = firestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH);

        Log.d(TAG, "Value of inviter status before query is " + mInviterIsOnTeam);
        teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // If the query returned anything, the user is on a team.
                            if (task.getResult().size() > 0) {
                                mInviterIsOnTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d(TAG, "Value of inviter after query is " + mInviterIsOnTeam);
    }

    /**
     * Sets the invitee's invite status to true if this user exists on a team, false otherwise
     *
     * @param user
     * @param firestore
     */
    private void setInviteeIsOnTeam(IUser user, FirebaseFirestore firestore) {
        CollectionReference teamsCol = firestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH);

        Log.d(TAG, "Value of invitee before query is " + mInviteeIsOnTeam);
        teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // If the query returned anything, the user is on a team.
                            if (task.getResult().size() > 0) {
                                mInviteeIsOnTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d(TAG, "Value of invitee after query is " + mInviteeIsOnTeam);
    }

    private void addUserToTeam(IUser user, FirebaseFirestore firestore) {
        CollectionReference teamCol = firestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH);
        teamCol.document(user.getEmail()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
}
