package com.example.wwrapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduleWalkScreenActivity extends AppCompatActivity {

    private static String TAG = "ScheduleWalkScreenActivity";

    private Button scheduleButton;
    private Button withdrawButton;
    private AbstractUser mUser;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_walk_screen);
        mFirestore = FirebaseFirestore.getInstance();
        scheduleButton.findViewById(R.id.scheduleBtn);
        withdrawButton.findViewById(R.id.withdrawBtn);
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleWalkScreenActivity.this, ProposedWalkActivity.class);
                intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
                startActivity(intent);
            }
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                        .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });
    }
}
