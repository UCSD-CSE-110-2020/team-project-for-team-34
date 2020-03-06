package com.example.wwrapp.models;

/**
 * Represents a Team invitation
 */
public class TeamInvitation {

    public static final String FIELD_INVITER = "inviter";
    public static final String FIELD_INVITEE = "invitee";
    public static final String FIELD_INVITATION_STATUS = "invitationStatus";

    private String inviterEmail;
    private String inviteeEmail;
    private String invitationStatus;

    public TeamInvitation(String inviter, String invitee, String invitationStatus) {
        this.inviterEmail = inviter;
        this.inviteeEmail = invitee;
        this.invitationStatus = invitationStatus;
    }

    public String getInviterEmail() {
        return inviterEmail;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }
}
