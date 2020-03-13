package com.example.wwrapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.ProposeWalk;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ScheduleWalkScreenActivity extends AppCompatActivity {

    private static String TAG = "ScheduleWalkScreenActivity";

    private Button scheduleButton;
    private Button withdrawButton;
    private AbstractUser mUser;
    private FirebaseFirestore mFirestore;

    private ProposeWalk mWalk;
    private Route mRoute;
    private TextView timeView;
    private TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_walk_screen);
        mFirestore = FirebaseFirestore.getInstance();
        scheduleButton = findViewById(R.id.scheduleBtn);
        withdrawButton = findViewById(R.id.withdrawBtn);
        mUser = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

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
                            findViewById(R.id.close_route_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Close up this activity
                                    Log.d(TAG, "Clicked 'X' button");
                                    finish();
                                }
                            });

                            timeView = findViewById(R.id.proposedDateTextView_schedule);
                            dateView = findViewById(R.id.proposedTimeTextView_scheduled);

                            Intent intent = getIntent();
                            mRoute = mWalk.getRoute();
                            mUser = (AbstractUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

                            TextView startingPointText = findViewById(R.id.starting_point_text_view_schedule);
                            startingPointText.setText(mRoute.getStartingPoint());

                            TextView routeDateText = findViewById(R.id.route_detail_date_schedule);
                            routeDateText.setText(mRoute.getDateOfLastWalk());

                            double miles = mRoute.getMiles();
                            TextView routeMilesText = findViewById(R.id.miles_text_view_schedule);
                            routeMilesText.setText(String.valueOf(miles));

                            long steps = mRoute.getSteps();
                            TextView routeStepsText = findViewById(R.id.steps_text_view_schedule);
                            routeStepsText.setText(String.valueOf(steps));

                            TextView noteText = findViewById(R.id.notes_text_view_schedule);
                            noteText.setText(mRoute.getNotes());

                            // Display the tags
                            List<String> tags = mRoute.getTags();
                            if (tags != null) {
                                int i = 1;
                                for (String tag : tags) {
                                    TextView tagText;
                                    switch (i) {
                                        case 1:
                                            tagText = findViewById(R.id.tag1);
                                            tagText.setText(tag);
                                            break;
                                        case 2:
                                            tagText = findViewById(R.id.tag2);
                                            tagText.setText(tag);
                                            break;
                                        case 3:
                                            tagText = findViewById(R.id.tag3);
                                            tagText.setText(tag);
                                            break;
                                        case 4:
                                            tagText = findViewById(R.id.tag4);
                                            tagText.setText(tag);
                                            break;
                                        case 5:
                                            tagText = findViewById(R.id.tag5);
                                            tagText.setText(tag);
                                            break;
                                        default:
                                            break;
                                    }
                                    i++;
                                }
                            }

                            timeView.setText(mWalk.getTime());
                            dateView.setText(mWalk.getDate());
                        }
                    }
                });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleWalkScreenActivity.this, RoutesActivity.class);
                intent.putExtra(WWRConstants.EXTRA_USER_KEY, mUser);
                intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_SCHEDULE_WALK_ACTIVITY_CALLER_ID);
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

