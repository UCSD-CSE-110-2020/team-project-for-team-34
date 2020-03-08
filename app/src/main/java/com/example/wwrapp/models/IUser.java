package com.example.wwrapp.models;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a user of the WWR app
 */
public interface IUser extends Serializable {

    public String getName();
    public String getEmail();
    public String getInviterEmail();
    public String getTeamName();

    public List<Route> getRoutes();
    public String getStatus();

    public void setInviterEmail(String newInviter);
    public void setTeamName(String newTeamName);

    public void addInvitees(IUser user);
    public void setRoutes(List<Route> newRoutes);

    public void setStatus(String status);
}
