package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.MockUser;
import com.example.wwrapp.model.TeamInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InviteMemberScreenActivity extends AppCompatActivity {
    private static final String TAG = "InviteMemberScreenActivity";

    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private TextView mMemberText;
    private String mMemberName;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        // Get the database instance
        mFirestore = FirebaseFirestore.getInstance();

        mMemberText = findViewById(R.id.team_member_name_text_view);
        // get the member's name from data base and set it to member_name
        // TODO: Re-design invitations

        Intent intent = getIntent();
        IUser invitee = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        String inviteeEmail = invitee.getEmail();

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

        mAcceptBtn = findViewById(R.id.invite_accept_button);
        mDeclineBtn = findViewById(R.id.invite_decline_button);
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
                // go to team route but user's info will not be stored.
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
}
