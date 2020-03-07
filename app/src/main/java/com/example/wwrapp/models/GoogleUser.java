package com.example.wwrapp.models;

import com.example.wwrapp.CustomQuery.UserQuery;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleUser implements IUser, Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITER = "inviter";
    public static final String FIELD_TEAMNAME = "teamName";
    public static final String FIELD_INVITEES = "invitees";
    public static final String FIELD_ROUTES = "routes";

    public static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    public static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    public static final String STRING_DEFAULT = "";

    private String mName;
    private String mEmail;
    private String mInviter;
    private String mteamName;
    private List<String> mInvitees;
    private List<Route> mRoutes;
//    Map<String, Object> user = new HashMap<>();
//        user.put(WWRConstants.USER_EMAIL_KEY, iUser.getEmail());
//        user.put(WWRConstants.USER_NAME_KEY, iUser.getName());
//        user.put(WWRConstants.USER_INVITER_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
//        user.put(WWRConstants.USER_TEAM_KEY, WWRConstants.DEFAULT_DATABASE_VALUE);
//        user.put(WWRConstants.USER_INVITEES_KEY, WWRConstants.USER_DEFAULT_INVITEES_VALUES);
//        user.put(WWRConstants.USER_ROUTES_KEY, WWRConstants.USER_DEFAULT_ROUTE_VALUE);

    public GoogleUser(String name, String email) {
        mName = name;
        mEmail = email;
        mInviter = STRING_DEFAULT;
        mteamName = STRING_DEFAULT;
        mInvitees = INVITEES_DEFAULT;
        mRoutes = ROUTES_DEFAULT;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public String getInviterEmail() {
        return mInviter;
    }

    @Override
    public String getTeamName() {
        return mteamName;
    }

    @Override
    public List<String> getInvitees() {
        return mInvitees;
    }

    @Override
    public List<Route> getRoutes() {
        return mRoutes;
    }
}
