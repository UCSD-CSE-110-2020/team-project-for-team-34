package com.example.wwrapp.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProposeWalk {

    private Route route;
    private List<ProposeWalkUser> users;
    private String owner;
    private String date;

    public ProposeWalk() {}

    public ProposeWalk(Route route, String owner) {
        this.route = route;
        this.owner = owner;
        users = new ArrayList<>();
    }

    public void addUser(String userEmail) {
        if(userEmail != owner) {
            ProposeWalkUser user = new ProposeWalkUser(userEmail);
            users.add(user);
        }
    }

    public void addUser(IUser user) {
        if(user.getEmail() != owner) {
            ProposeWalkUser propUser = new ProposeWalkUser(user);
            users.add(propUser);
        }
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

    public List<ProposeWalkUser> getUsers() {
        return users;
    }
}
