package com.example.wwrapp.activities;

import android.app.Activity;
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
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.IUserFactory;
import com.example.wwrapp.models.MockUser;
import com.example.wwrapp.models.TeamMember;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Case when inviter sent out invitations and gets merged onto invitee's team
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

    private IUser mInviter;
    private IUser mInvitee;

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
        mInviter = (IUser) (intent.getSerializableExtra(WWRConstants.EXTRA_USER_KEY));

        Log.i(TAG, "inviter  email is " + mInviter.getEmail());


        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInviteeName = mInviteeNameEditText.getText().toString();
                mInviteeEmail = mInviteeEmailEditText.getText().toString();
                Log.d(TAG, "Invitee name on click is: " + mInviteeName);
                Log.d(TAG, "Invitee email on click is: " + mInviteeEmail);
                TeamMember teamMember = new TeamMember(mInviteeEmail, FirestoreConstants.FIRESTORE_TEAM_INVITE_PENDING, mInviteeName);

                // Save member_name and member_email to database and go to team screen.

                // check if invitee is on the firebase or not, if it is, pull it down, else create a new object
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviteeEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                mInvitee = document.toObject(MockUser.class);
                                Log.d(TAG, "DocumentSnapshot data for invitee: " + document.getData());
                            } else {
                                mInvitee = IUserFactory.createUser(WWRConstants.MOCK_USER_FACTORY_KEY,
                                        mInviteeName,
                                        mInviteeEmail);
                                onInviteeIsNotInFirestore();
                                Log.d(TAG, "No such document for invitee");
                            }

                            // TODO:
                            onInviteeComplete(teamMember);

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                // Go to the Team screen
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to previous screen
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setInviteeExists() {
        inviteeExist = true;
    }

    private void setInviteeNotExists() {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    /**
     * Adds invitee to Firestore and sets inviterEmail and status
     */
    public void onInviteeIsNotInFirestore() {
        Log.d(TAG, "sequenceInviteeIsOnTeam: ");
        // if invitee does not exist on firebase, add it to the collection.

        mInvitee.setStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_PENDING);
        mInvitee.setInviterEmail(mInviter.getEmail());
        mInvitee.setInviterName(mInviter.getName());

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH).document(mInvitee.getEmail())
                .set(mInvitee)
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

    public void onInviteeComplete(TeamMember inviteeTeamMember) {
        Log.d(TAG, "onInviterIsNotOnTeam: ");
        // If Inviter is not on a team
        if (mInviter.getTeamName().isEmpty()) {
            Log.d(TAG, "Inviter is on team: ");


            // If invitee is not already on a team and inviter is not on a team
            if (mInvitee.getTeamName().isEmpty()) {
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviter.getEmail())
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                        .document(mInviteeEmail).set(inviteeTeamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Added invitee to inviter's invitees!");

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@android.support.annotation.NonNull Exception e) {
                                Log.e(TAG, "Error writing document", e);
                            }
                        });
            } else {
                // If invitee is already on a team and inviter is not on team
                TeamMember inviterTeamMember =
                        new TeamMember(mInviter.getEmail(), FirestoreConstants.FIRESTORE_TEAM_INVITE_PENDING, mInviter.getName());

                // Add inviter to team members
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                        .document(mInviter.getEmail())
                        .set(inviterTeamMember)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "Inviter added to teamMembers collection!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@android.support.annotation.NonNull Exception e) {
                                Log.e(TAG, "Error writing inviter to teamMembers", e);
                            }
                        });

                // Update invitee (on team already) email
                mFirestore.collection(FirestoreConstants.USERS_COLLECITON_KEY)
                        .document(mInviteeEmail).update(MockUser.FIELD_INVITER_NAME, mInviter.getName(),
                        MockUser.FIELD_INVITER_EMAIL, mInviter.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully updated invitee's email");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        } else {
            Log.d(TAG, "Inviter is on team: ");
            Log.d(TAG, "invitee is not on team: ");
            // If inviter is on team and invitee is not on team
            if (mInvitee.getTeamName().isEmpty()) {
                // If inviter is on team and invitee is not on team

                // Add invitee to teamMembers
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                        .document(mInviteeEmail)
                        .set(inviteeTeamMember)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "Invitee added to teamMembers collection!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@android.support.annotation.NonNull Exception e) {
                                Log.e(TAG, "Error writing invitee to teamMembers", e);
                            }
                        });

                // Set the inviter email
                mFirestore.collection(FirestoreConstants.USERS_COLLECITON_KEY)
                        .document(mInviteeEmail).update(MockUser.FIELD_INVITER_NAME,
                        mInviter.getName(),
                        MockUser.FIELD_INVITER_EMAIL, mInviter.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully set inviter name and email for invitee");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            } else {
                // If both inviter and invitee are on team
                Log.d(TAG, "Both the inviter and invitee are on a team: ");
                Toast.makeText(AddTeamMemberActivity.this,
                        "Both the inviter and invitee are on a team, try a different email", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
