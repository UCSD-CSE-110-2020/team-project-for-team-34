package com.example.wwrapp.CustomQuery;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.wwrapp.activities.HomeScreenActivity;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserQuery {
    private static boolean mTempUserExists;
    public static FirebaseFirestore mFirestore;
    private static final String TAG = "UserQuery";


    public UserQuery() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static void createUser( IUser iUser) {
        if (userExists()) {
            return;
        }
        boolean isEmailLoaded = false;
        while (!isEmailLoaded) {
            isEmailLoaded = HomeScreenActivity.account.getDisplayName() != null;

        }
        Map<String, Object> user = new HashMap<>();
        user.put(WWRConstants.USER_EMAIL_KEY, iUser.getEmail());
        user.put(WWRConstants.USER_NAME_KEY, iUser.getName());
        user.put(WWRConstants.USER_ROUTES_OWNED_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        user.put(WWRConstants.USER_ROUTES_NOT_OWNED_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        user.put(WWRConstants.USER_TEAM_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
        mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_USER_PATH).document(iUser.getEmail()).set(user);
    }

    private static boolean userExists() {
        CollectionReference usersCollection = mFirestore.collection(WWRConstants.FIRESTORE_COLLECTION_INVITATIONS_PATH);
        usersCollection
                .whereEqualTo(WWRConstants.USER_EMAIL_KEY, HomeScreenActivity.account.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            userExist();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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

}
