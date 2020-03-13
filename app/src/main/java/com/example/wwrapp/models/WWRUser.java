package com.example.wwrapp.models;

/**
 * Concrete user
 */
public class WWRUser extends AbstractUser {

    // Default constructor required for Firestore
    public WWRUser() {

    }

    public WWRUser(String name, String email, String teamName, String teamStatus) {
        super(name, email, teamName, teamStatus);
    }
}
