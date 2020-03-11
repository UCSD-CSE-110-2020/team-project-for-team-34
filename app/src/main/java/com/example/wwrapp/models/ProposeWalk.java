package com.example.wwrapp.models;

import java.util.List;

public class ProposeWalk {

    private Route route;
    private List<ProposeWalkUser> userEmails;
    private String date;

    public ProposeWalk() {}

    public ProposeWalk(Route route) {
        this.route = route;
    }

    public void addUser(String userEmail) {
        userEmails.add(new ProposeWalkUser(userEmail));
    }

    public void addUser(IUser user) {
        userEmails.add(new ProposeWalkUser(user));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public String getDate() {
        return date;
    }

    public List<ProposeWalkUser> getUserEmails() {
        return userEmails;
    }
}
