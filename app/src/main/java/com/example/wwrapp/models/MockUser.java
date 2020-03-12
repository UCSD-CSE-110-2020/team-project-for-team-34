package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;

import java.util.List;

/**
 * A mock user for testing
 */
public class MockUser extends AbstractUser {

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
        super();

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
    public boolean equals(Object o) {
        if ((o instanceof AbstractUser)) {
            AbstractUser user = (AbstractUser) o;
            return name.equals(user.getName());
        } else {
            return false;
        }
    }
}
