package com.example.wwrapp.models;

import java.io.Serializable;

/**
 * Represents a user of the WWR app
 */
public interface IUser extends Serializable {

    public String getName();
    public String getEmail();
    public String getInviterName();
    public String getInviterEmail();

    public void setInviterName(String inviterName);
    public void setInviterEmail(String newInviter);

    public String getTeamName();
    public void setTeamName(String newTeamName);

    public String getStatus();
    public void setStatus(String status);
}
