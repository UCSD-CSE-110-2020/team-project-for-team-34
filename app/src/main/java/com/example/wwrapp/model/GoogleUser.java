package com.example.wwrapp.models;

import com.example.wwrapp.model.IUser;
import com.example.wwrapp.model.Route;
import com.example.wwrapp.WWRConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GoogleUser implements IUser, Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INVITER = "inviter";
    public static final String FIELD_TEAMNAME = "teamName";
    public static final String FIELD_INVITEES = "invitees";
    public static final String FIELD_ROUTES = "routes";
    public static final String FIELD_STATUS = "status";

    public static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    public static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    public static final String STRING_DEFAULT = "";

    private String mName;
    private String mEmail;
    private String mInviter;
    private String mteamName;
    private List<String> mInvitees;
    private List<Route> mRoutes;
    private String status;

    public GoogleUser(){

    }

    public GoogleUser(String name, String email) {
        mName = name;
        mEmail = email;
        status = WWRConstants.FIRESTORE_TEAM_INVITE_ACCEPTED;
        mInviter = STRING_DEFAULT;
        mteamName = STRING_DEFAULT;
        mInvitees = INVITEES_DEFAULT;
        mRoutes = ROUTES_DEFAULT;
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
        return mName;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public String getInviterEmail() {
        return mInviter;
    }

    @Override
    public String getTeamName() {
        return mteamName;
    }

    @Override
    public List<String> getInvitees() {
        return mInvitees;
    }

    @Override
    public List<Route> getRoutes() {
        return mRoutes;
    }

    @Override
    public void setInviterEmail(String newInviter) {
        mInviter = newInviter;
    }

    @Override
    public void setTeamName(String newTeamName){
        mteamName = newTeamName;
    }

    @Override
    public void setInvitees(List<String> newInvitees) {
        mInvitees = newInvitees;
    }

    @Override
    public void addInvitees(String userEmail) {
        mInvitees.add(userEmail);
    }

    @Override
    public void setRoutes(List<Route> newRoutes) {
        mRoutes = newRoutes;
    }

    @Override
    public void updateRoute(Route newRoute) {
        ListIterator<Route> itr = mRoutes.listIterator();
        while (itr.hasNext()) {
            if( itr.next().getRouteName().equals(newRoute.getRouteName()) ) {
                itr.set(newRoute);
            }
        }
    }


    @Override
    public void addRoutes(Route newRoutes) {
        mRoutes.add(newRoutes);
    }

    @Override
    public void removeInvitee(String email) {
        ListIterator<String> itr = mInvitees.listIterator();
        while (itr.hasNext()) {
            if( itr.next().equals(email) ) {
                itr.remove();
            }
        }
    }
}
