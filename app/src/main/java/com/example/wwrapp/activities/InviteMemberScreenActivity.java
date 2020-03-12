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
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.MockUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.TeamMember;
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
    private IUser mUser;
    private IUser mInviter;

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

        // Set team name
        mUser.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);
        mInviter.setTeamName(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH);

        // Create team members
        TeamMember inviterTeamMember = new TeamMember(mInviter.getEmail(),
                FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED,
                mInviter.getName());

        TeamMember inviteeTeamMember = new TeamMember(mUser.getEmail(),
                FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED,
                mUser.getName());

        // Store team members

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mInviter.getEmail())
                .set(inviterTeamMember)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully added inviter to team!");

                Log.d(TAG, "Adding inviter's routes to team");


                // Add this team member's routes to the team
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mInviter.getEmail())
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Route route = document.toObject(Route.class);

                                        String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mInviter.getEmail(), route.getRouteName());
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
                            }
                        });

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

                Log.d(TAG, "Adding invitee's routes to team");

                // Add this team member's routes to the team
                mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                        .document(mUser.getEmail())
                        .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Route route = document.toObject(Route.class);

                                        String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mUser.getEmail(), route.getRouteName());
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
                            }
                        });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        // Update the team name in Firestore

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

        // Update invitee's member status
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mUser.getEmail()).update(TeamMember.FIELD_STATUS, FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Invitee status successfully updated!");

                        // Add this team member's routes to the team
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                                .document(mUser.getEmail())
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                Route route = document.toObject(Route.class);

                                                String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mUser.getEmail(), route.getRouteName());
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
                                    }
                                });



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
        // Update inviter's team member status
        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_MEMBERS_PATH)
                .document(mInviter.getEmail()).update(TeamMember.FIELD_STATUS, FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully updated inviter status in invitee's team members collection");

                        // Add this team member's routes to the team
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                                .document(mInviter.getEmail())
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                Route route = document.toObject(Route.class);

                                                String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mInviter.getEmail(), route.getRouteName());
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
                                    }
                                });


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

        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
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
