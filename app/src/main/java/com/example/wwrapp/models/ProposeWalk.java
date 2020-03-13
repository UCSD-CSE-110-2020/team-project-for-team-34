package com.example.wwrapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProposeWalk implements Serializable {

    private Route route;
    private List<ProposeWalkUser> users;
    private String proposerEmail; // email
    private String date;
    private String time;
    private String proposerName;
    private String status;

    public ProposeWalk() {}

    public ProposeWalk(Route route, String proposerEmail, String proposerName) {
        this.route = route;
        this.proposerEmail = proposerEmail;
        this.proposerName = proposerName;
        users = new ArrayList<>();
    }

    public String getProposerEmail() {
        return proposerEmail;
    }

    public void addUser(String userEmail) {
        if(!userEmail.equals(proposerEmail)) {
            ProposeWalkUser user = new ProposeWalkUser(userEmail);
            users.add(user);
        }
    }

    public void addUser(AbstractUser user) {
        if(!user.getEmail().equals(proposerEmail)) {
            ProposeWalkUser propUser = new ProposeWalkUser(user);
            users.add(propUser);
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setUserReason(AbstractUser user, int reason) {
        for( int i = 0; i < users.size() ; i++ ) {
            ProposeWalkUser propUser = users.get(i);
            if(propUser.getEmail().equals(user.getEmail())) {
                propUser.setReason(reason);
                users.set(i, propUser);
            }
        }
    }

    public Route getRoute() {
        return route;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public List<ProposeWalkUser> getUsers() {
        return users;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
