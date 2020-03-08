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

    private static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    private static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    private static final String STRING_DEFAULT = "";

    private String name;
    private String email;
    private String inviter;
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
        inviter = STRING_DEFAULT;
        teamName = STRING_DEFAULT;
        invitees = INVITEES_DEFAULT;
        routes = ROUTES_DEFAULT;
    }

    @Override
    public String getStatus() {
        return status;
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
    public String getInviterEmail() {
        return inviter;
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
        inviter = newInviter;
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
