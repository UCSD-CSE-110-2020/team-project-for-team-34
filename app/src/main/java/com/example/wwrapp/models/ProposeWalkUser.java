package com.example.wwrapp.models;

import com.example.wwrapp.utils.WWRConstants;

import java.io.Serializable;

public class ProposeWalkUser implements Serializable {

    private static boolean DEFAULT_PENDING = true;
    public static int DEFAULT_REASON = 0;

    public static String FIELD_NAME = "name";
    public static String FIELD_EMAIL = "email";


    private String name;
    private String email;
    private boolean isPending;
    private int reason;

    public ProposeWalkUser(){}

    public ProposeWalkUser(AbstractUser user) {
        name = user.getName();
        email = user.getEmail();
        isPending = DEFAULT_PENDING;
        reason = DEFAULT_REASON;
    }

    public ProposeWalkUser(String userEmail, String userName) {
        name = userName;
        email = userEmail;
        isPending = DEFAULT_PENDING;
        reason = DEFAULT_REASON;
    }

    public int getReason() {
        return reason;
    }

    public boolean getIsPending() {
        return isPending;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setReason(int reason) {
        if(reason == WWRConstants.PROPOSED_WALK_ACCEPT_STATUS ||
           reason == WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS ||
           reason == WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS) {
            this.reason = reason;
            isPending = false;
        }
    }
}
