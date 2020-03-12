package com.example.wwrapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wwrapp.R;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.ProposeWalk;
import com.example.wwrapp.models.ProposeWalkUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProposedWalkActivity extends AppCompatActivity {
    public static String TAG = "ProposedWalkActivity";

    private TextView timeView;
    private TextView dateView;
    private Button acceptButton;
    private Button badTimeButton;
    private Button badRouteButton;

    private ProposeWalk mWalk;
    private Route mRoute;
    private IUser mUser;
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            mWalk = document.toObject(ProposeWalk.class);
                            setContentView(R.layout.activity_proposed_walk);
                            timeView = findViewById(R.id.proposedDateTextView);
                            dateView = findViewById(R.id.proposedTimeTextView);
                            acceptButton = findViewById(R.id.acceptBtn);
                            badTimeButton = findViewById(R.id.badTimeBtn);
                            badRouteButton = findViewById(R.id.badRouteBtn);

                            Intent intent = getIntent();
                            mRoute = mWalk.getRoute();
                            mUser = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

                            timeView.setText(mWalk.getTime());
                            dateView.setText(mWalk.getDate());

                            acceptButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mWalk.setUserReason(mUser, WWRConstants.PROPOSED_WALK_ACCEPT_STATUS);
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                            .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).set(mWalk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Status set to accept");
                                                    finish();
                                                }
                                            });
                                }
                            });

                            badTimeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mWalk.setUserReason(mUser, WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS);
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                            .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).set(mWalk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Status set to bad time");
                                                    finish();
                                                }
                                            });
                                }
                            });

                            badRouteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mWalk.setUserReason(mUser, WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS);
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                            .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).set(mWalk)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Status set to bad route");
                                                    finish();
                                                }
                                            });
                                }
                            });
                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });
    }

}
