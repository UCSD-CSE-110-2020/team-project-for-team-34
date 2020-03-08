package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

    public GoogleUser() {}

    public GoogleUser(String name, String email) {

        this.name = name;
        this.email = email;
        status = FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED;
        inviter = STRING_DEFAULT;
        teamName = STRING_DEFAULT;
        invitees = INVITEES_DEFAULT;
        routes = ROUTES_DEFAULT;
    }

    @Override
    public String getStatus(){
        return status;
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
    public String getInviterEmail() {
        return inviter;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override

    public List<Route> getRoutes() {
        return routes;
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
    public void setInvitees(List<String> newInvitees) {
        invitees = newInvitees;
    }

    @Override
    public void addInvitees(IUser user) {
        invitees.add(user.getEmail());
    }

    @Override
    public void setRoutes(List<Route> newRoutes) {
        routes = newRoutes;
    }

    @Override
    public void updateRoute(Route newRoute) {
        ListIterator<Route> itr = routes.listIterator();
        while (itr.hasNext()) {
            if( itr.next().getRouteName().equals(newRoute.getRouteName()) ) {
                itr.set(newRoute);
            }
        }
    }


    @Override
    public void addRoutes(Route newRoutes) {
        routes.add(newRoutes);
    }

    @Override
    public void removeInvitee(String email) {
        ListIterator<String> itr = invitees.listIterator();
        while (itr.hasNext()) {
            if( itr.next().equals(email) ) {
                itr.remove();
            }
        }
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

