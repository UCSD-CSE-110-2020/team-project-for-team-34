package com.example.wwrapp.CustomQuery;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.wwrapp.activities.HomeScreenActivity;
import com.example.wwrapp.models.GoogleUser;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserQuery {
    private static boolean mTempUserExists;
    public static FirebaseFirestore mFirestore;
    private static final String TAG = "UserQuery";
    private static IUser mUser;


    public UserQuery() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static void firstTimeSaveUser(IUser iUser) {
        if (userExists()) {
            return;
        }
        boolean isEmailLoaded = false;
        while (!isEmailLoaded) {
            isEmailLoaded = HomeScreenActivity.account.getDisplayName() != null;

        }
        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(iUser.getEmail()).set(iUser);
    }

    public static void overwriteUser(IUser iUser) {
        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(iUser.getEmail()).set(iUser);
    }

    private static boolean userExists() {
        DocumentReference findUser = mFirestore.collection(WWRConstants.USERS_COLLECITON_KEY).document(HomeScreenActivity.account.getEmail());
        findUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        userExist();
                    } else {
                        Log.d(TAG, "No such document");
                        userDoesNotExist();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    userDoesNotExist();
                }
                userDoesNotExist();
            }
        });
        return mTempUserExists;
    }

    public static void userExist() {
        mTempUserExists = true;
    }

    public static void userDoesNotExist() {
        mTempUserExists = false;
    }

    public static IUser getUser(String email) {
        DocumentReference userRef = UserQuery.mFirestore.collection("users").document(email);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        IUser user = document.toObject(IUser.class);
                        updateMUser(user);
                    } else {
                        Log.d(TAG, "Could not find user " + email);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return mUser;
    }

    private static void updateMUser(IUser user) {
        mUser = user;
    }
}
