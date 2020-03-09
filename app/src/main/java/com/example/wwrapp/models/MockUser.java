package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock user for testing
 */
public class MockUser implements IUser {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITE_STATUS = "inviteStatus";
    public static final String FIELD_INVITER_NAME = "inviterName";
    public static final String FIELD_INVITER_EMAIL = "inviterEmail";
    public static final String FIELD_TEAM_NAME = "teamName";



    private static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    private static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    public static final String STRING_DEFAULT = "";

    private String name;
    private String email;
    private String inviterName;
    private String inviterEmail;
    private String teamName;
    private List<String> invitees;
    private List<Route> routes;
    private String status;

    public MockUser() {
    }

    public MockUser(String name, String email) {

        this.name = name;
        this.email = email;
        status = FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED;
        inviterName = STRING_DEFAULT;
        inviterEmail = STRING_DEFAULT;
        teamName = STRING_DEFAULT;
        invitees = INVITEES_DEFAULT;
        routes = ROUTES_DEFAULT;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getInviterName() {
        return inviterName;
    }

    @Override
    public String getInviterEmail() {
        return inviterEmail;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public void setInviterEmail(String newInviter) {
        inviterEmail = newInviter;
    }

    @Override
    public void setTeamName(String newTeamName) {
        teamName = newTeamName;
    }

    @Override
    public void addInvitees(IUser user) {
        invitees.add(user.getEmail());
    }

    @Override
    public void setRoutes(List<Route> newRoutes) {
        routes = newRoutes;
    }


    @Override
    public boolean equals(Object o) {
        if ((o instanceof IUser)) {
            IUser user = (IUser) o;
            return name.equals(user.getName());
        } else {
            return false;
        }
    }
}
