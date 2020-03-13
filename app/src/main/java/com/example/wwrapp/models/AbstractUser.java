package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RandomColorGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract user of the WWR app
 */
public abstract class AbstractUser implements Serializable {
    // The names of these fields as they appear on Firestore
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITER_NAME = "inviterName";
    public static final String FIELD_INVITER_EMAIL = "inviterEmail";
    public static final String FIELD_TEAM_NAME = "teamName";
    public static final String FIELD_TEAM_STATUS = "teamStatus";
    public static final String FIELD_WALK_STATUS = "walkStatus";
    public static final String FIELD_COLOR = "color";




    public static final String STRING_DEFAULT = "";

    static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    private String name;
    private String email;
    private String inviterName;
    private String inviterEmail;
    private String teamName;
    private String teamStatus;
    private String walkStatus;
    private int color;


    // Empty constructor, needed for Firestore
    public AbstractUser() {

    }

    public AbstractUser(String name, String email, String teamName, String teamStatus) {
        this.name = name;
        this.email = email;
        this.teamName = teamName;
        this.teamStatus = teamStatus;

        color = RandomColorGenerator.generateRandomNum();

        // Default values for un-set fields
        this.inviterName = FirestoreConstants.FIRESTORE_DEFAULT_INVITER_NAME;
        this.inviterEmail = FirestoreConstants.FIRESTORE_DEFAULT_INVITER_EMAIL;
        this.walkStatus = FirestoreConstants.FIRESTORE_DEFAULT_WALK_STATUS;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviterName() {
        return inviterName;
    }
    public String getInviterEmail() {
        return inviterEmail;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public void setInviterEmail(String inviterEmail) {
        this.inviterEmail = inviterEmail;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamStatus() {
        return teamStatus;
    }

    public void setTeamStatus(String teamStatus) {
        this.teamStatus = teamStatus;
    }

    public String getWalkStatus() {
        return walkStatus;
    }

    public void setWalkStatus(String walkStatus) {
        this.walkStatus = walkStatus;
    }

    public int getColor() {
        return this.color;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FIELD_NAME).append(": ").append(name).append("\n")
                .append(FIELD_EMAIL).append(": ").append(email).append("\n")
                .append(FIELD_INVITER_NAME).append(": ").append(inviterName).append("\n")
                .append(FIELD_INVITER_EMAIL).append(": ").append(inviterEmail).append("\n")
                .append(FIELD_TEAM_NAME).append(": ").append(teamName).append("\n")
                .append(FIELD_TEAM_STATUS).append(": ").append(teamStatus).append("\n")
                .append(FIELD_WALK_STATUS).append(": ").append(walkStatus).append("\n");
        return stringBuilder.toString();
    }
}
