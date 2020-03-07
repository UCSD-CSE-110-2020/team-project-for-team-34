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
    public List<String> getInvitees();
    public List<Route> getRoutes();

}
