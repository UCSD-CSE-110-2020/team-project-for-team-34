package com.example.wwrapp.models;

import java.util.Map;

public class Team {

    public static final String FIELD_EMAIL_MAP = "members";

    private String teamName;

    private Map<String, Boolean> members;

    public Team() {
    }

    public Team(Map<String, Boolean> emailMap) {
        this.members = emailMap;
    }

    public String getTeamName() {
        return teamName;
    }


    public void setTeamName(String newTeamName) {
        teamName = newTeamName;
    }


    public Map<String, Boolean> getMembers() {
        return members;
    }
}
