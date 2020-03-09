package com.example.wwrapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.models.IUser;
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

public class InviteMemberScreenActivity extends AppCompatActivity {
    private static final String TAG = "InviteMemberScreenActivity";

    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private TextView mMemberText;
    private String mInviterName;
    private FirebaseFirestore mFirestore;
    private IUser mUser;
    private IUser mInviter;

    // For testing purposes
    private static boolean testInvite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        // Get the database instance
        mFirestore = FirebaseFirestore.getInstance();

        // Set up display
        mMemberText = findViewById(R.id.team_member_name_text_view);
        mAcceptBtn = findViewById(R.id.invite_accept_button);
        mDeclineBtn = findViewById(R.id.invite_decline_button);

        // get user and inviter
        mUser = (IUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
        assert mUser != null;
        mInviterName = mUser.getInviterName();
        String inviterEmail = mUser.getInviterEmail();
        Log.d(TAG, "Inviter name is " + mInviterName);
        Log.d(TAG, "Inviter email is " + inviterEmail);

        // find inviter object in database
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(inviterEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Got inviter data: " + document.getData());
                                mInviter = (document.toObject(MockUser.class));
                            } else {
                                Log.d(TAG, "Couldn't find inviter");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        // TODO: Fix this code to not have testInvite set
        if (testInvite) {
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
            // Find the users who have invited the invitee
            // TODO: Handle multiple inviters (not just 1)
            mMemberText.setText(mInviterName);

            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If both the inviter and invitee are not on a team
                    if (mUser.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()) {
                        onInviterAndInviteeNotOnTeamAccept();
                        // If the inviter is on a team and the invitee is not on a team
                    } else if (mUser.getTeamName().isEmpty()) {
                        onInviterOnTeamAndInviteeNotOnTeamAccept();
                        // if the inviter is not on a team and the invitee is on a team
                    } else if (!mUser.getTeamName().isEmpty()) {
                        onInviterNotOnTeamAndInviteeOnTeamAccept();
                    } else {
                        Log.w(TAG, "Unhandled in 4 cases");
                    }

                    // Return to Team screen
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });

            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If both the inviter and invitee are not on a team
                    if (mUser.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()) {
                        onInviterAndInviteeNotOnTeamDecline();
                        // If the inviter is on a team and the invitee is not on a team
                    } else if (mUser.getTeamName().isEmpty()) {
                        onInviterOnTeamAndInviteeNotOnTeamDecline();
                        // if the inviter is not on a team and the invitee is on a team
                    } else if (!mUser.getTeamName().isEmpty()) {
                        onInviterNotOnTeamAndInviteeOnTeamDecline();
                    } else {
                        Log.w(TAG, "Unhandled in 4 cases");
                    }

                    // Go back to team screen
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }


    }

    public static void testInvite(boolean testInviteMember) {
        testInvite = testInviteMember;
    }

    public void onInviterAndInviteeNotOnTeamAccept() {
        Log.d(TAG, "onInviterAndInviteeNotOnTeam: ");
        mUser.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInviter.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);

        TeamMember inviterTeamMember = new TeamMember(mInviter.getEmail(),
                FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED,
                mInviter.getName());

        TeamMember inviteeTeamMember = new TeamMember(mUser.getEmail(),
                FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED,
                mUser.getName());

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mInviter.getEmail())
                .set(inviterTeamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully added inviter to team!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mUser.getEmail())
                .set(inviteeTeamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully added invitee to team!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        // Update the team name
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail()).set(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully updated invitee's team name");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mInviter.getEmail()).set(mInviter).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully updated inviter's team name");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        // Reset invitee's inviter Email
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail())
                .update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT, MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reset invitee's inviter name and email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }




    public void onInviterOnTeamAndInviteeNotOnTeamAccept() {
        Log.d(TAG, "onInviterAndInviteeNotOnTeam: ");
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mUser.getEmail()).update(TeamMember.FIELD_STATUS, FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Invitee status successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        // Update team name on the "user"
        mUser.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail()).set(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully updated invitee's team name");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        // Reset invitee's inviter Email
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail())
                .update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT, MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reset invitee's inviter name and email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    public void onInviterNotOnTeamAndInviteeOnTeamAccept() {
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mInviter.getEmail()).update(TeamMember.FIELD_STATUS, FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully updated inviter to invitee's team members collection");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        // Reset invitee's inviter Email
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail())
                .update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT, MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reset invitee's inviter name and email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void onInviterAndInviteeNotOnTeamDecline() {
        Log.d(TAG, "onInviterAndInviteeNotOnTeamDecline: ");
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail()).update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT,
                MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully reset invitee's inviter name and email");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mInviter.getEmail()).collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                .document(mUser.getEmail()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Deleted invitee from inviter's list");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void onInviterOnTeamAndInviteeNotOnTeamDecline() {
        Log.d(TAG, "onInviterOnTeamAndInviteeNotOnTeamDecline: ");
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail()).update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT,
                MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully reset invitee's inviter name and email");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mFirestore.collection(FirestoreConstants.TEAMS_COLLECTION_KEY)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mUser.getEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Deleted invitee from team members map!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public void onInviterNotOnTeamAndInviteeOnTeamDecline() {
        Log.d(TAG, "onInviterNotOnTeamAndInviteeOnTeamDecline: ");
        // Delete inviter from team members
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mInviter.getEmail()).delete() .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Deleted invitee from team members map!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        // Reset invitee's inviter email
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mUser.getEmail())
                .update(MockUser.FIELD_INVITER_NAME, MockUser.STRING_DEFAULT, MockUser.FIELD_INVITER_EMAIL, MockUser.STRING_DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reset invitee's inviter name and email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        // Remove invitee from inviter's sub-collection
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(mInviter.getEmail())
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                .document(mUser.getEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Deleted invitee from inviter's list");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }
}
