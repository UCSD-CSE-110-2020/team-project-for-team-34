package com.example.wwrapp.models;

import com.example.wwrapp.utils.WWRConstants;

public class ProposeWalkUser {

    private static boolean DEFAULT_PENDING = true;
    private static int DEFAULT_REASON = 0;

    private String email;
    private boolean isPending;
    private int reason;

    public ProposeWalkUser(){}

    public ProposeWalkUser(IUser user) {
        email = user.getEmail();
        isPending = DEFAULT_PENDING;
        reason = DEFAULT_REASON;
    }

    public ProposeWalkUser(String userEmail) {
        email = userEmail;
        isPending = DEFAULT_PENDING;
        reason = DEFAULT_REASON;
    }

    public int getReason() {
        return reason;
    }

    public Boolean getIsPending() {
        return isPending;
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
