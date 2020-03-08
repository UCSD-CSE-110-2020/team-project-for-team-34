package com.example.wwrapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.IUserFactory;
import com.example.wwrapp.models.MockUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Check that users are added to the database correctly.
 */
public class AddTeamMemberActivity extends AppCompatActivity {
    private static final String TAG = "AddTeamMemberActivity";

    private Button mConfirmBtn;
    private Button mCancelBtn;
    private TextView mNewMemberNameTextView;
    private TextView mNewMemberEmailTextView;
    private EditText mInviteeNameEditText;
    private EditText mInviteeEmailEditText;
    private String mInviteeName;
    private String mInviteeEmail;

    private FirebaseFirestore mFirestore;
    private boolean mInviterIsOnTeam;
    private boolean mInviteeIsOnTeam;
    private boolean inviteeExist;
    private IUser invitee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Log.d(TAG, "in method onCreate");

        // Display view elements
        mNewMemberNameTextView = findViewById(R.id.add_member_name_text_view);
        mNewMemberEmailTextView = findViewById(R.id.add_member_email_text_view);

        mInviteeNameEditText = findViewById(R.id.member_name_edit_text);
        mInviteeEmailEditText = findViewById(R.id.member_email_edit_text);

        mInviteeName = mInviteeNameEditText.getText().toString();
        mInviteeEmail = mInviteeEmailEditText.getText().toString();
        mConfirmBtn = findViewById(R.id.add_member_button);
        mCancelBtn = findViewById(R.id.add_member_cancel_button);

        // Get database instance
        mFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String userType = intent.getStringExtra(WWRConstants.EXTRA_USER_TYPE_KEY);
        assert userType != null;
        IUser user = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        Log.i(TAG, "user email is " + user.getEmail());


        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInviteeName = mInviteeNameEditText.getText().toString();
                mInviteeEmail = mInviteeEmailEditText.getText().toString();
                Log.d(TAG, "Invitee name on click is: " + mInviteeName);
                Log.d(TAG, "Invitee email on click is: " + mInviteeEmail);

                // Save member_name and member_email to database and go to team screen.


                // Check if the inviter is already in a team
//                setInviterIsOnTeam(inviter, mFirestore);

                //CollectionReference userCol = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH);
                Log.d(TAG, "Value of inviter on team before query is " + mInviterIsOnTeam);


                // Try to find invitee
                DocumentReference userRef = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH).document(mInviteeEmail);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                setInviteeExists();
                                invitee = (IUser) (document.getData().get(mInviteeEmail));
                            } else {
                                Log.d(TAG, "No such document");
                                setInviteeNotExists();
                                invitee = IUserFactory.createUser(WWRConstants.MOCK_USER_FACTORY_KEY, mInviteeName, mInviteeEmail);
                                sequenceInviteeIsOnTeam();
                            }

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                if (true) {
                    return;
                }

                Log.d(TAG, "past the return");





                // if invitee and inviter are both on team, prevent them from inviting.
                if(!invitee.getTeamName().isEmpty() && !user.getTeamName().isEmpty()) {
                    Toast.makeText(AddTeamMemberActivity.this,
                            "Enter a different email since" + invitee.getName() + " is already on a team",
                            Toast.LENGTH_SHORT).show();
                } else {
                    user.addInvitees(invitee);
                    invitee.setInviterEmail(user.getEmail());
                    invitee.setStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_PENDING);
                }


//                teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, inviter.getEmail()).get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "Query for inviter was successful");
//                                    // If the query returned anything, the user is on a team.
//                                    mInviterIsOnTeam = task.getResult().size() > 0;
//
//                                    // Add the inviter to the team, if they don't belong to the team already
//                                    if (!mInviterIsOnTeam) {
//
//                                        teamsCol.document(inviter.getEmail()).set(inviter).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "DocumentSnapshot for inviter successfully written!");
//
//
//                                                // Check if invitee is on team
//                                                teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, finalInvitee.getEmail()).get()
//                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    Log.d(TAG, "Query for invitee was successful");
//                                                                    // If the query returned anything, the user is on a team.
//                                                                    mInviteeIsOnTeam = task.getResult().size() > 0;
//
//                                                                    if (!mInviteeIsOnTeam) {
//                                                                            teamsCol.document(finalInvitee.getEmail()).set(finalInvitee).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                @Override
//                                                                                public void onSuccess(Void aVoid) {
//                                                                                    Log.d(TAG, "DocumentSnapshot  for invitee successfully written!");
//
//
//                                                                                    // Create an invitation so that the inviter's name can be checked.
//                                                                                    TeamInvitation teamInvitation = new TeamInvitation(inviter.getEmail(),
//                                                                                            finalInvitee.getEmail(), WWRConstants.FIRESTORE_TEAM_INVITE_PENDING);
//                                                                                    CollectionReference teamInvitationsCol =
//                                                                                            mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);
//                                                                                    teamInvitationsCol.add(teamInvitation)
//                                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                                                                @Override
//                                                                                                public void onSuccess(DocumentReference documentReference) {
//                                                                                                    Log.d(TAG, "DocumentSnapshot for teamInvitations written with ID: " + documentReference.getId());
//
//
//                                                                                                }
//                                                                                            })
//                                                                                            .addOnFailureListener(new OnFailureListener() {
//                                                                                                @Override
//                                                                                                public void onFailure(@NonNull Exception e) {
//                                                                                                    Log.w(TAG, "Error adding document", e);
//                                                                                                }
//                                                                                            });
//
//
//                                                                                }
//
//                                                                            })
//                                                                                    .addOnFailureListener(new OnFailureListener() {
//                                                                                @Override
//                                                                                public void onFailure(@NonNull Exception e) {
//                                                                                    Log.w(TAG, "Error writing document", e);
//                                                                                }
//                                                                            });
//                                                                    }
//
//                                                                } else {
//                                                                    Log.d(TAG, "Error getting documents: ", task.getException());
//                                                                }
//                                                            }
//                                                        });
//
//
//                                            }
//                                        })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Log.w(TAG, "Error writing document", e);
//                                                    }
//                                                });
//
//
//                                    }
//
//
//                                } else {
//                                    Log.d(TAG, "Error getting documents: ", task.getException());
//                                }
//                            }
//                        });


//                // Check if the invitee is already invited (i.e. already in a team)
//                setInviteeIsOnTeam(invitee, mFirestore);
//                // Add the invitee to the team, if they don't belong to the team already
//                if (!mInviteeIsOnTeam) {
//                    Log.d(TAG, "Invitee isn't on a team, adding to team now");
////                    addUserToTeam(invitee, invitee.getEmail(), mFirestore);
//                    mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_TEAMS_PATH).document("TEMP").set(invitee).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully written!");
//                        }
//                    })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error writing document", e);
//                                }
//                            });
//                }
//
//                // Create an invitation so that the inviter's name can be checked.
//                TeamInvitation teamInvitation = new TeamInvitation(inviter.getEmail(),
//                        invitee.getEmail(), WWRConstants.FIRESTORE_TEAM_INVITE_PENDING);
//                CollectionReference teamInvitationsCol =
//                        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);
//                teamInvitationsCol.add(teamInvitation)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
//

                // Go to the Team screen
                //setResult(Activity.RESULT_OK);
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to previous screen
                //setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setInviteeExists(){
        inviteeExist = true;
    }
    private void setInviteeNotExists(){
        inviteeExist = false;
    }



    /**
     * Sets the inviter's invite status to true if this user exists on a team, false otherwise
     *
     * @param user
     * @param firestore
     */
    private void setInviterIsOnTeam(IUser user, FirebaseFirestore firestore) {
        CollectionReference teamsCol = firestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH);

        Log.d(TAG, "Value of inviter on team before query is " + mInviterIsOnTeam);
        teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Query for inviter was successful");
                            // If the query returned anything, the user is on a team.
                            if (task.getResult().size() > 0) {
                                mInviterIsOnTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d(TAG, "Value of inviter on team after query is " + mInviterIsOnTeam);
    }

    /**
     * Sets the invitee's invite status to true if this user exists on a team, false otherwise
     *
     * @param user
     * @param firestore
     */
    private void setInviteeIsOnTeam(IUser user, FirebaseFirestore firestore) {
        CollectionReference teamsCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH);

        Log.d(TAG, "Value of invitee on team before query is " + mInviteeIsOnTeam);
        teamsCol.whereEqualTo(MockUser.FIELD_EMAIL, user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Query for invitee was successful");
                            // If the query returned anything, the user is on a team.
                            if (task.getResult().size() > 0) {
                                mInviteeIsOnTeam = true;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d(TAG, "Value of invitee on team after query is " + mInviteeIsOnTeam);
    }

    private void addUserToTeam(IUser user, String email, FirebaseFirestore firestore) {
        Log.d(TAG, "in method addUserToTeam");
        Map<String, Object> map = new HashMap<>();
        map.put("FIELD", "VALUE");
        Log.d(TAG, "user email is " + email);


        if (true) {
            return;
        }
        CollectionReference teamCol = mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH);
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

    public void sequenceInviteeIsOnTeam() {
        Log.d(TAG, "sequenceInviteeIsOnTeam: ");
        // if invitee does not exist on firebase, add it to the collection.
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH).document(invitee.getEmail())
                .set(invitee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
