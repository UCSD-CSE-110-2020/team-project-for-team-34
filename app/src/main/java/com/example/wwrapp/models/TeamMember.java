package com.example.wwrapp.models;

/**
 * Represents a team member-status pair
 */
public class TeamMember {

    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_NAME = "name";


    private String email;
    private String status;
    private String name;

    public TeamMember() {

    }

    public TeamMember(String email, String status, String name) {
        this.email = email;
        this.status = status;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
