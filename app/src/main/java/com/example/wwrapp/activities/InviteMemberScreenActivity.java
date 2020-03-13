package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.WWRUser;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InviteMemberScreenActivity extends AppCompatActivity {
    private static final String TAG = "InviteMemberScreenActivity";

    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private TextView mMemberText;
    private String mInviterName;
    private FirebaseFirestore mFirestore;
    private AbstractUser mInvitee;
    private AbstractUser mInviter;

    // For testing purposes
    private static boolean testInvite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        // Set up display
        mMemberText = findViewById(R.id.team_member_name_text_view);
        mAcceptBtn = findViewById(R.id.invite_accept_button);
        mDeclineBtn = findViewById(R.id.invite_decline_button);


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
            // Get the database instance
            mFirestore = FirebaseFirestore.getInstance();

            // get user and inviter
            mInvitee = (AbstractUser) (getIntent().getSerializableExtra(WWRConstants.EXTRA_USER_KEY));
            assert mInvitee != null;
            mInviterName = mInvitee.getInviterName();
            String inviterEmail = mInvitee.getInviterEmail();
            Log.d(TAG, "Inviter name is " + mInviterName);
            Log.d(TAG, "Inviter email is " + inviterEmail);

            // find inviter object in database
            mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                    .document(inviterEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    mInviter = (document.toObject(WWRUser.class));
                                    Log.d(TAG, "Got inviter data:\n" + mInviter.toString());

                                } else {
                                    Log.d(TAG, "Couldn't find inviter");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

            // Set name of inviter on display
            mMemberText.setText(mInviterName);

            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Case 1: If both the inviter and invitee are not on a team
                    if (mInvitee.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()) {
                        onInviterAndInviteeNotOnTeamAccept();
                        // Case 2: If the inviter is on a team and the invitee is not on a team
                    } else if (mInvitee.getTeamName().isEmpty()) {
                        onInviterOnTeamAndInviteeNotOnTeamAccept();
                        // Case 3: if the inviter is not on a team and the invitee is on a team
                    } else if (!mInvitee.getTeamName().isEmpty()) {
                        onInviterNotOnTeamAndInviteeOnTeamAccept();
                    } else {
                        // Case 4
                        Log.w(TAG, "Both inviter and invitee are on team! This SHOULD NOT HAPPEN!");
                    }

                    // Return to Team screen
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(WWRConstants.EXTRA_USER_TEAM_NAME_KEY, FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }
            });

            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If both the inviter and invitee are not on a team
                    if (mInvitee.getTeamName().isEmpty() && mInviter.getTeamName().isEmpty()) {
                        onInviterAndInviteeNotOnTeamDecline();
                        // If the inviter is on a team and the invitee is not on a team
                    } else if (mInvitee.getTeamName().isEmpty()) {
                        onInviterOnTeamAndInviteeNotOnTeamDecline();
                        // if the inviter is not on a team and the invitee is on a team
                    } else if (!mInvitee.getTeamName().isEmpty()) {
                        onInviterNotOnTeamAndInviteeOnTeamDecline();
                    } else {
                        Log.w(TAG, "Unhandled in 4 cases");
                    }

                    // Go back to team screen
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
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
        // Here mUser is the invitee

        // Set team name
        mInvitee.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInvitee.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);
        mInviter.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInviter.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

        // Reset invitee's inviter Email
        resetUserInviterAndInviteeEmail(mInvitee);

        // Remove invitee from inviter's list of invitees
        removeInviteeFromInviterList(mInvitee, mInviter);

        // Add team members
        writeUserAndRoutesToTeam(mInvitee);
        writeUserAndRoutesToTeam(mInviter);

        // Update individual users
        writeUserToUsersCollection(mInvitee);
        writeUserToUsersCollection(mInviter);
    }


    public void onInviterOnTeamAndInviteeNotOnTeamAccept() {
        Log.d(TAG, "onInviterAndInviteeNotOnTeam: ");

        mInvitee.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInvitee.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

        // mInviter teamName is already set
        mInviter.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

        // Reset invitee's inviter Email
        resetUserInviterAndInviteeEmail(mInvitee);

        // Update invitee and routes in team
        writeUserAndRoutesToTeam(mInvitee);
        // Update invitee in users
        writeUserToUsersCollection(mInvitee);
    }

    public void onInviterNotOnTeamAndInviteeOnTeamAccept() {
        // Reset invitee's inviter Email
        resetUserInviterAndInviteeEmail(mInvitee);

        // Update inviter's team member status
        mInviter.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInviter.setTeamStatus(FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED);

        // Add inviter and routes to team
        writeUserAndRoutesToTeam(mInviter);
        writeUserToUsersCollection(mInviter);

        // Delete invitee from inviter's list
        removeInviteeFromInviterList(mInvitee, mInviter);
    }

    public void onInviterAndInviteeNotOnTeamDecline() {
        Log.d(TAG, "onInviterAndInviteeNotOnTeamDecline: ");

        // Reset invitee's inviter info
        resetUserInviterAndInviteeEmail(mInvitee);

        // Delete invitee from inviter's list
        removeInviteeFromInviterList(mInvitee, mInviter);
    }

    public void onInviterOnTeamAndInviteeNotOnTeamDecline() {
        Log.d(TAG, "onInviterOnTeamAndInviteeNotOnTeamDecline: ");
        resetUserInviterAndInviteeEmail(mInvitee);
        removeUserFromTeamMembers(mInvitee);
    }

    public void onInviterNotOnTeamAndInviteeOnTeamDecline() {
        Log.d(TAG, "onInviterNotOnTeamAndInviteeOnTeamDecline: ");
        removeUserFromTeamMembers(mInviter);
        resetUserInviterAndInviteeEmail(mInvitee);
        removeInviteeFromInviterList(mInvitee, mInviter);
    }

    private void writeUserToUsersCollection(AbstractUser user) {
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(user.getEmail())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    }

    private void writeUserAndRoutesToTeam(AbstractUser user) {
        Log.d(TAG, "writeInviteeAndRoutesToFirestore: ");

        // Add invitee and routes to team
        // Add invitee
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(user.getEmail())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully added user to team!");

                        // Add this team member's routes to the team
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                                .document(user.getEmail())
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Adding user's routes to team");
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                Route route = document.toObject(Route.class);

                                                String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(user.getEmail(), route.getRouteName());
                                                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                        .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                                        .document(routeDocName)
                                                        .set(route)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "Successfully added route to team collection!");
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
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    } // end onComplete
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing invitee", e);
                    }
                });
    }

    private void resetUserInviterAndInviteeEmail(AbstractUser user) {
        user.setInviterName(AbstractUser.STRING_DEFAULT);
        user.setInviterEmail(AbstractUser.STRING_DEFAULT);

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(user.getEmail())
                .update(AbstractUser.FIELD_INVITER_NAME,
                        AbstractUser.STRING_DEFAULT,
                        AbstractUser.FIELD_INVITER_EMAIL,
                        AbstractUser.STRING_DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reset user's inviter name and email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void removeInviteeFromInviterList(AbstractUser invitee, AbstractUser inviter) {
        // Remove invitee from inviter's sub-collection
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                .document(inviter.getEmail())
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_INVITEES_PATH)
                .document(invitee.getEmail())
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

    private void removeUserFromTeamMembers(AbstractUser user) {
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(user.getEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Deleted user from team members map!");
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
