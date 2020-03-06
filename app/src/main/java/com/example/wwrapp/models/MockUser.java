package com.example.wwrapp.models;

import java.io.Serializable;

/**
 * A mock user for testing
 */
public class MockUser implements IUser, Serializable {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITE_STATUS = "inviteStatus";

    private String name;
    private String email;
    private String inviteStatus;

    public MockUser(String name, String email, String inviteStatus) {
        this.name = name;
        this.email = email;
        this.inviteStatus = inviteStatus;
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
    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }
}
