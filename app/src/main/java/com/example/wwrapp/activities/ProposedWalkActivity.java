package com.example.wwrapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.ProposeWalk;
import com.example.wwrapp.models.ProposeWalkUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.ProposedWalkStatusCodeUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProposedWalkActivity extends AppCompatActivity {
    public static String TAG = "ProposedWalkActivity";

    private static String ROUTE_DELETED_IN_MEANTIME_TOAST_TEXT = "This route has been withdrawn by the proposer";

    private TextView timeView;
    private TextView dateView;
    private Button acceptButton;
    private Button badTimeButton;
    private Button badRouteButton;

    private Button mWithdrawBtn;
    private Button mScheduleBtn;

    private TextView mTitleTextView;
    private TextView mWalkStatusTextView;

    private LinearLayout mInviteeLinearLayout;
    private int mIndexOfCurrentUser;

    private ProposeWalk mWalk;
    private Route mRoute;
    private AbstractUser mUser;
    private FirebaseFirestore mFirestore;

    public static boolean MOCKING = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!MOCKING) {
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
                                setContentView(R.layout.activity_proposed_walk);
                                mWalk = document.toObject(ProposeWalk.class);
                                findViewById(R.id.close_route_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Close up this activity
                                        Log.d(TAG, "Clicked 'X' button");
                                        finish();
                                    }
                                });

                                mRoute = mWalk.getRoute();
                                Intent intent = getIntent();
                                mUser = (AbstractUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

                                // Display UI elements

                                // Title and status
                                mTitleTextView = findViewById(R.id.route_detail_name);
                                mTitleTextView.setText(mRoute.getRouteName());

                                mWalkStatusTextView = findViewById(R.id.proposeOrScheduled);
                                mWalkStatusTextView.setText(mWalk.getStatus());


                                // Get UI elements for setting later
                                timeView = findViewById(R.id.proposedDateTextView);
                                dateView = findViewById(R.id.proposedTimeTextView);
                                acceptButton = findViewById(R.id.acceptBtn);
                                badTimeButton = findViewById(R.id.badTimeBtn);
                                badRouteButton = findViewById(R.id.badRouteBtn);
                                mScheduleBtn = findViewById(R.id.scheduleBtn);
                                mWithdrawBtn = findViewById(R.id.withdrawBtn);
                                mInviteeLinearLayout = findViewById(R.id.response_Layout);

                                // Set invitees
                                List<ProposeWalkUser> invitees = mWalk.getUsers();
                                for (int i = 0; i < invitees.size(); i++) {
                                    ProposeWalkUser invitee = invitees.get(i);

                                    // If current user matches a person on the invitee list,
                                    // save their index so we can update it later
                                    if (mUser.getEmail().equals(invitee.getEmail())) {
                                        mIndexOfCurrentUser = i;
                                    }

                                    TextView userTextView = new TextView(ProposedWalkActivity.this);
                                    String userAndStatus = ProposedWalkStatusCodeUtils.
                                            getUserAndStatusDisplay(invitee.getName(), invitee.getReason());
                                    userTextView.setText(userAndStatus);
                                    userTextView.setLayoutParams(new ViewGroup.LayoutParams
                                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    mInviteeLinearLayout.addView(userTextView);
                                }

                                // If user is proposer
                                if (mWalk.getProposerEmail().equals(mUser.getEmail())) {
                                    // Hide accept, decline buttons
                                    acceptButton.setVisibility(View.GONE);
                                    badRouteButton.setVisibility(View.GONE);
                                    badTimeButton.setVisibility(View.GONE);
                                } else {
                                    // If user is invitee
                                    mScheduleBtn.setVisibility(View.GONE);
                                    mWithdrawBtn.setVisibility(View.GONE);
                                }


                                TextView startingPointText = findViewById(R.id.starting_point_text_view);
                                startingPointText.setText(mRoute.getStartingPoint());


                                // For scheduled walks, enable Google Maps
                                if (mWalk.getStatus().equals(FirestoreConstants.FIRESTORE_ROUTE_STATUS_SCHEDULED)) {
                                    startingPointText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Open starting location on google maps
                                            String startingPoint = startingPointText.getText().toString();
                                            Log.d(TAG, "Transfer over to Google Maps with query of " + startingPoint);
                                            Uri query = Uri.parse("geo:0,0?q=" + startingPoint);
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, query);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            startActivity(mapIntent);
                                        }
                                    });

                                }

                                TextView routeDateText = findViewById(R.id.route_detail_date);
                                routeDateText.setText(mRoute.getDateOfLastWalk());

                                double miles = mRoute.getMiles();
                                TextView routeMilesText = findViewById(R.id.miles_text_view);
                                routeMilesText.setText(String.valueOf(miles));

                                long steps = mRoute.getSteps();
                                TextView routeStepsText = findViewById(R.id.steps_text_view);
                                routeStepsText.setText(String.valueOf(steps));

                                TextView noteText = findViewById(R.id.notes_text_view);
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

                                acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mWalk.setUserReason(mUser, WWRConstants.PROPOSED_WALK_ACCEPT_STATUS);

                                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {

                                                                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                                        .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                                        .set(mWalk)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "Status set to accept");
                                                                                View userTextView = mInviteeLinearLayout.getChildAt(mIndexOfCurrentUser);
                                                                                String userAndStatus =
                                                                                        ProposedWalkStatusCodeUtils
                                                                                                .getUserAndStatusDisplay(mUser.getName(),
                                                                                                        WWRConstants.PROPOSED_WALK_ACCEPT_STATUS);
                                                                                ((TextView) (userTextView)).setText(userAndStatus);
                                                                            }
                                                                        });
                                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                            } else {
                                                                Log.w(TAG, "Document was deleted in meantime");
                                                                Toast.makeText(ProposedWalkActivity.this, ROUTE_DELETED_IN_MEANTIME_TOAST_TEXT,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
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
                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                                        .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).set(mWalk)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "Status set to bad time");
                                                                                View userTextView = mInviteeLinearLayout.getChildAt(mIndexOfCurrentUser);

                                                                                String userAndStatus =
                                                                                        ProposedWalkStatusCodeUtils
                                                                                                .getUserAndStatusDisplay(mUser.getName(),
                                                                                                        WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS);
                                                                                ((TextView) (userTextView)).setText(userAndStatus);
                                                                            }
                                                                        });
                                                            } else {
                                                                Log.w(TAG, "Document was deleted in meantime");
                                                                Toast.makeText(ProposedWalkActivity.this, ROUTE_DELETED_IN_MEANTIME_TOAST_TEXT,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
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
                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {


                                                                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                                        .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK).set(mWalk)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "Status set to bad route");
                                                                                View userTextView = mInviteeLinearLayout.getChildAt(mIndexOfCurrentUser);

                                                                                String userAndStatus =
                                                                                        ProposedWalkStatusCodeUtils
                                                                                                .getUserAndStatusDisplay(mUser.getName(),
                                                                                                        WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS);
                                                                                ((TextView) (userTextView)).setText(userAndStatus);
                                                                            }
                                                                        });
                                                            } else {
                                                                Log.w(TAG, "Document was deleted in meantime");
                                                                Toast.makeText(ProposedWalkActivity.this, ROUTE_DELETED_IN_MEANTIME_TOAST_TEXT,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
                                                    }
                                                });


                                    }
                                });

                                mScheduleBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mWalk.setStatus(FirestoreConstants.FIRESTORE_ROUTE_STATUS_SCHEDULED);
                                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                .set(mWalk)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                        Log.d(TAG, "Successfully updated route status to scheduled");
                                                        mWalkStatusTextView.setText(FirestoreConstants.FIRESTORE_ROUTE_STATUS_SCHEDULED);
                                                        startingPointText.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                // Open starting location on google maps
                                                                String startingPoint = startingPointText.getText().toString();
                                                                Log.d(TAG, "Transfer over to Google Maps with query of " + startingPoint);
                                                                Uri query = Uri.parse("geo:0,0?q=" + startingPoint);
                                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, query);
                                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                                startActivity(mapIntent);
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });

                                mWithdrawBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        mWalk.setStatus(FirestoreConstants.FIRESTORE_ROUTE_STATUS_WITHDRAWN);
                                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                .set(mWalk)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                        Log.d(TAG, "Successfully updated route status to withdrawn");
                                                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_PROPOSED_WALK_PATH)
                                                                .document(FirestoreConstants.FIRE_STORE_DOCUMENT_PROPOSED_WALK)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "Deleted proposed walk!");
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
                                });


                            } else {
                                Log.d(TAG, "Cached get failed: ", task.getException());
                            }
                        }
                    });
        } else {
            setContentView(R.layout.activity_proposed_walk);
        }


    }
}
