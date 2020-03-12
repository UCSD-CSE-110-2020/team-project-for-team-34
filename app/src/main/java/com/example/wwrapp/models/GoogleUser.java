package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RandomColorGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GoogleUser implements IUser, Serializable {

    private static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    private static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    private static final String STRING_DEFAULT = "";

    private String name;
    private String email;
    private String inviter;
    private String teamName;
    private List<String> invitees;
    private List<Route> routes;
    private String status;
    private int color;

    public GoogleUser() {}

    public GoogleUser(String name, String email) {

        this.name = name;
        this.email = email;
        color = RandomColorGenerator.generateRandomNum();
        status = FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED;
        inviter = STRING_DEFAULT;
        teamName = STRING_DEFAULT;
        invitees = INVITEES_DEFAULT;
        routes = ROUTES_DEFAULT;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getStatus(){
        return status;
    }

    @Override
    public void setInviterName(String inviterName) {
    }

    @Override
    public void setStatus(String status){
        this.status = status;
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
    public String getInviterName() {
        return null;
    }

    @Override
    public String getInviterEmail() {
        return inviter;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public void setInviterEmail(String newInviter) {
        inviter = newInviter;
    }

    @Override
    public void setTeamName(String newTeamName){
        teamName = newTeamName;
    }


    @Override
    public boolean equals(Object o) {
        if( (o instanceof IUser) ) {
            IUser user = (IUser) o;
            return name.equals(user.getName());
        }
        else {
            return false;
        }
    }
}

