package com.example.wwrapp.utils;

public class ProposedWalkStatusCodeUtils {

    public static String getStatusFromCode(int statusCode) {
        String status = null;
        switch (statusCode) {
            case WWRConstants.PROPOSED_WALK_PENDING_STATUS:
                status = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_PENDING;
                break;
            case WWRConstants.PROPOSED_WALK_ACCEPT_STATUS:
                status = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_ACCEPTED;
                break;
            case WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS:
                status = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_NOT_A_GOOD_ROUTE;
                break;
            case WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS:
                status = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_BAD_TIME;
                break;
        }
        return status;
    }

    public static String getUserAndStatusDisplay(String name, int statusCode) {
        return name + ": " + getStatusFromCode(statusCode);
    }
}
