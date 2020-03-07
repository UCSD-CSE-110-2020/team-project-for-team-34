package com.example.wwrapp.models;

import java.io.Serializable;
import java.util.List;

/**
 * A mock user for testing
 */
public class MockUser implements IUser, Serializable {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITE_STATUS = "inviteStatus";

    private String name;
    private String email;
    private String teamName;

    public MockUser(String name, String email) {
        this.name = name;
        this.email = email;
        this.teamName = "team";
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
        return null;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public List<String> getInvitees() {
        return null;
    }

    @Override
    public List<Route> getRoutes() {
        return null;
    }

}
