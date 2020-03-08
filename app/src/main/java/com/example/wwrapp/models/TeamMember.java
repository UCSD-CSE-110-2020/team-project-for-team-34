package com.example.wwrapp.models;

/**
 * Represents a team member-status pair
 */
public class TeamMember {

    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_STATUS = "status";


    private String email;
    private String status;

    public TeamMember() {

    }

    public TeamMember(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
