package com.example.wwrapp.models;

import android.util.Log;

import com.example.wwrapp.utils.WWRConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ProposeWalk implements Serializable {

    private Route route;
    private List<ProposeWalkUser> users;
    private String owner;
    private String date;
    private String time;

    public ProposeWalk() {}

    public ProposeWalk(Route route, String owner) {
        this.route = route;
        this.owner = owner;
        users = new ArrayList<>();
    }

    public String getOwner() {
        return owner;
    }

    public void addUser(String userEmail) {
        if(userEmail.equals(owner)) {
            ProposeWalkUser user = new ProposeWalkUser(userEmail);
            users.add(user);
        }
    }

    public void addUser(IUser user) {
        if(user.getEmail().equals(owner)) {
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

    public void setUserReason(IUser user, int reason) {
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
}
