package com.example.wwrapp.models;

import java.util.List;

/**
 * A mock user for testing
 */
public class MockUser implements IUser{

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

    
    public String getName() {
        return name;
    }

    
    public String getEmail() {
        return email;
    }

    
    public String getInviterEmail() {
        return null;
    }

    
    public String getTeamName() {
        return teamName;
    }


    public List<Route> getRoutes() {
        return null;
    }

    
    public void setInviterEmail(String newInviter) {
        return;
    }

    
    public void setTeamName(String newTeamName){
        return;
    }

    
    public void setInvitees(List<String> newInvitees) {
        return;
    }

    
    public void addInvitees(IUser user) {
        return;
    }

    
    public void setRoutes(List<Route> newRoutes) {
        return;
    }

    
    public void addRoutes(Route route) {
        return;
    }

    
    public void updateRoute(Route newRoute) { return; }

    
    public void removeInvitee(String email) {
        return;
    }

    
    public void setStatus(String status) { return; }

    
    public String getStatus() {return null; }

}
