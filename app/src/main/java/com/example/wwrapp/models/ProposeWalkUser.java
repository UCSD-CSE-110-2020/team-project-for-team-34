package com.example.wwrapp.models;

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

        isPending = false;
    }
}
