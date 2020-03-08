package com.example.wwrapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.example.wwrapp.models.IUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InviteMemberScreenActivity extends AppCompatActivity {
    private static final String TAG = "InviteMemberScreenActivity";

    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private TextView mMemberText;
    private String mMemberName;
    private FirebaseFirestore mFirestore;
    private IUser user;
    private IUser inviter;

    // For testing purposes
    private static boolean testInvite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        // Get the database instance
        mFirestore = FirebaseFirestore.getInstance();

        mMemberText = findViewById(R.id.team_member_name_text_view);
        mAcceptBtn = findViewById(R.id.invite_accept_button);
        mDeclineBtn = findViewById(R.id.invite_decline_button);

        // get user and inviter
        user = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        String inviterEmail = user.getInviterEmail();
        // find inviter object in database
        DocumentReference userRef = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH).document(inviterEmail);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        inviter = (IUser)(document.getData().get(inviterEmail));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        // TODO: Probably need to fix this later
        if(testInvite){
            String name = "Ariana";
            mMemberText.setText(name);
            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {

            // get the member's name from data base and set it to member_name
            // TODO: Re-design invitations


            // Find the users who have invited the invitee
            // TODO: Handle multiple inviters (not just 1)
            mMemberName = inviter.getName();
            mMemberText.setText(mMemberName);


            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to team route and set user in the team to be onTeam
                    // set teamName.

                    // generate new team name and set it to both users
                    if(user.getTeamName().isEmpty() && inviter.getTeamName().isEmpty()) {
                        Map<String, Boolean> newTeamMap = new HashMap<>();
                        newTeamMap.put(user.getEmail(), true);
                        newTeamMap.put(inviter.getEmail(), true);
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_NAME)
                                .set(newTeamMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        user.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_NAME);
                        inviter.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_NAME);
                    } else if(user.getTeamName().isEmpty() && !inviter.getTeamName().isEmpty()){
                        inviter.setStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

                    }

                    // Return to Team screen
                    finish();
                }
            });

            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Just go to the Team screen or TODO: handle more invites

                    // Go back to team screen
                    finish();
                }
            });
        }


    }

    public static void testInvite(boolean testInviteMember){
        testInvite = testInviteMember;
    }
}
