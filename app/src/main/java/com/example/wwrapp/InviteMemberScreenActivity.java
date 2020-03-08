package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.models.GoogleUser;
import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.MockUser;
import com.example.wwrapp.model.TeamInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteMemberScreenActivity extends AppCompatActivity {
    private static final String TAG = "InviteMemberScreenActivity";

    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private TextView mMemberText;
    private String mMemberName;
    private FirebaseFirestore mFirestore;
    private IUser mUser;
    private IUser mInviter;
    private Map<String, Object> map;

    // For testing purposes
    private static boolean testInvite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);



        // Get the database instance
        mFirestore = FirebaseFirestore.getInstance();
        map = new HashMap<>();

        mMemberText = findViewById(R.id.team_member_name_text_view);
        mAcceptBtn = findViewById(R.id.invite_accept_button);
        mDeclineBtn = findViewById(R.id.invite_decline_button);

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

            Intent intent = getIntent();
            mUser = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

            // Find the users who have invited the invitee
            // TODO: Handle multiple inviters (not just 1)

            // get inviter from database
            DocumentReference userCol = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(mUser.getInviterEmail());
            userCol.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mInviter = (IUser)(documentSnapshot.toObject(GoogleUser.class));
                }
            });

            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInviter.removeInvitee(mUser.getEmail());
                    mUser.setStatus(WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

                    // if both of them not on a team, create a team and store to database
                    if(mUser.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()){
                        map.put(mUser.getEmail(), (Boolean) true);
                        map.put(mInviter.getEmail(), (Boolean) true);
                    }
                    // if user on team and inviter not, then look for user's team on database and add inviter to team.
                    // also add all invitees of inviter in inviter's array to team
                    if(!mUser.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()){
                        DocumentReference teamCol =  mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document(mUser.getTeamName());
                        teamCol.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                map = (HashMap<String, Object>)(documentSnapshot.getData());
                            }
                        });
                        map.put(mInviter.getEmail(), (Boolean) true);

                        List<String> userInvitee = mInviter.getInvitees();
                        for(String inviteeEmail : userInvitee){
                            map.put(inviteeEmail, false);
                        }
                    }
                    // if user not on team and inviter on team, then locate team of inviter and set user status to accept.
                    if(mUser.getTeamName().isEmpty() && !mInviter.getTeamName().isEmpty()){
                        DocumentReference teamCol =  mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document(mUser.getTeamName());
                        teamCol.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                map = (HashMap<String, Object>)(documentSnapshot.getData());
                            }
                        });
                        map.put(mUser.getEmail(),(Boolean) true);
                    }

                    // now that we have done everything, we set user team name to be the same as inviter
                    // also set inviter email of user to be empty.
                    mUser.setTeamName(mInviter.getTeamName());
                    mUser.setInviterEmail("");

                    //save all changes
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(mUser.getEmail()).set(mUser);
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(mInviter.getEmail()).set(mInviter);
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document(WWRConstants.TEAM_NAME).set(map);

                    finish();
                }
            });

            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUser.setStatus(WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

                    // if inviter not on team, just remove user from inviter
                    if(mUser.getTeamName().isEmpty() ){
                        mInviter.removeInvitee(mUser.getEmail());
                    }
                    // if user not on team, and inviter on team, then locate team and remove user from map.
                    else if(mUser.getTeamName().isEmpty() && !mInviter.getTeamName().isEmpty()){
                        DocumentReference teamCol =  mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document(mUser.getTeamName());
                        teamCol.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                map = (HashMap<String, Object>)(documentSnapshot.getData());
                            }
                        });
                        map.remove(mUser.getEmail());
                    }

                    // now that we have done everything, we set user team name to be empty
                    // also set inviter email of user to be empty.
                    mUser.setTeamName("");
                    mUser.setInviterEmail("");

                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(mUser.getEmail()).set(mUser);
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(mInviter.getEmail()).set(mInviter);
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document(WWRConstants.TEAM_NAME).set(map);

                    finish();
                }
            });

        }
    }

    public static void testInvite(boolean testInviteMember){
        testInvite = testInviteMember;
    }
}

/* ORIGINAL CODE


            CollectionReference invitationsCol =
                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);
            invitationsCol.whereEqualTo(TeamInvitation.FIELD_INVITEE, inviteeEmail).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mMemberName = document.getString(TeamInvitation.FIELD_INVITER);
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            mMemberText.setText(mMemberName);


            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to team route and store user's info to team screen.
                    Intent intent = getIntent();
                    IUser invitee =  (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
                    CollectionReference teamCol = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH);
                    teamCol.document(invitee.getEmail())
                            .update(MockUser.FIELD_INVITE_STATUS, WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    // Return to Team screen
                    finish();
                }
            });

            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Just go to the Team screen or TODO: handle more invites
                    Intent intent = getIntent();
                    IUser invitee =  (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
                    CollectionReference teamCol = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH);
                    teamCol.document(invitee.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                    // Go back to team screen
                    finish();
                }
            });
        }


 */
