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
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.AbstractUserFactory;
import com.example.wwrapp.models.MockUser;
import com.example.wwrapp.models.TeamMember;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * TODO: Case when inviter sent out invitations and gets merged onto invitee's team
 */
public class AddTeamMemberActivity extends AppCompatActivity {
    private static final String TAG = "AddTeamMemberActivity";

    private static final String INVALID_NAME_OR_EMAIL_SUBMISSION_TOAST = "Please enter a name and an email";
    private static final String BOTH_INVITER_AND_INVITEE_ON_TEAM_TOAST =
            "Both the inviter and invitee are already on the same team";


    private Button mConfirmBtn;
    private Button mCancelBtn;
    private TextView mNewMemberNameTextView;
    private TextView mNewMemberEmailTextView;
    private EditText mInviteeNameEditText;
    private EditText mInviteeEmailEditText;
    private String mInviteeName;
    private String mInviteeEmail;

    private FirebaseFirestore mFirestore;

    private AbstractUser mInviter;
    private AbstractUser mInvitee;

    private static boolean disablemUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Log.d(TAG, "in method onCreate");

        if(disablemUser){
            findViewById(R.id.add_member_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.add_member_cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {

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

            // Retrieve this user
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

                    // If either of the name or email is blank, don't allow this invite to send
                    if (mInviteeName.isEmpty() || mInviteeEmail.isEmpty()) {
                        Toast.makeText(AddTeamMemberActivity.this,
                                INVALID_NAME_OR_EMAIL_SUBMISSION_TOAST, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

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
                                // TODO: User factory
                                mInvitee = AbstractUserFactory.createUser(WWRConstants.MOCK_USER_FACTORY_KEY,
                                        mInviteeName,
                                        mInviteeEmail,
                                        FirestoreConstants.FIRESTORE_DEFAULT_TEAM_NAME,
                                        FirestoreConstants.FIRESTORE_DEFAULT_TEAM_STATUS);
                                onInviteeIsNotInFirestore();
                                Log.d(TAG, "No such document for invitee");
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
    }

    /**
     * Adds invitee to Firestore and sets inviterEmail and status
     */
    public void onInviteeIsNotInFirestore() {
        Log.d(TAG, "sequenceInviteeIsOnTeam: ");
        // if invitee does not exist on firebase, add it to the collection.

        mInvitee.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_PENDING);
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
            Log.d(TAG, "Inviter is not on team: ");
            // If invitee is not already on a team and inviter is not on a team
            if (mInvitee.getTeamName().isEmpty()) {
                // Add invitee to inviter's list
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviter.getEmail())
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                        .document(mInviteeEmail).set(inviteeTeamMember)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
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

                // Add inviter to invitee's pending
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviteeEmail)
                        .update(AbstractUser.FIELD_INVITER_NAME, mInviter.getName(),
                                AbstractUser.FIELD_INVITER_EMAIL, mInviter.getEmail())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "Added inviter to invitee's pending!");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@android.support.annotation.NonNull Exception e) {
                                Log.e(TAG, "Error writing inviter to pending", e);
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
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviteeEmail).update(AbstractUser.FIELD_INVITER_NAME, mInviter.getName(),
                        AbstractUser.FIELD_INVITER_EMAIL, mInviter.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                // Add the invitee to the inviter's pending invitees
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviter.getEmail())
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                        .document(mInvitee.getEmail())
                        .set(inviteeTeamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Invitee added to inviter's invitees collection!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@android.support.annotation.NonNull Exception e) {
                                Log.e(TAG, "Error writing invitee to inviter's collection", e);
                            }
                        });
            } // end case : invitee is already on a team and inviter is not on team
        } else {
            Log.d(TAG, "Inviter is on team: ");
            Log.d(TAG, "invitee is not on team: ");
            // If inviter is on team and invitee is not on team
            if (mInvitee.getTeamName().isEmpty()) {
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
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviteeEmail).update(AbstractUser.FIELD_INVITER_NAME,
                        mInviter.getName(),
                        AbstractUser.FIELD_INVITER_EMAIL, mInviter.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        BOTH_INVITER_AND_INVITEE_ON_TEAM_TOAST, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public static void disableUser(boolean disable){
        disablemUser = disable;
    }

}
