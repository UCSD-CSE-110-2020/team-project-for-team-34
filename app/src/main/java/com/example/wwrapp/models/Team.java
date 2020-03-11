package com.example.wwrapp.models;

import java.util.Map;

public class Team {

    public static final ProposeWalk DEFAULT_PROPOSE_WALK = null;
    public static final String FIELD_EMAIL_MAP = "members";

    private String teamName;

    private Map<String, Boolean> members;

    private ProposeWalk proposeWalk;

    public Team() {}

    public Team(Map<String, Boolean> emailMap) {
        this.members = emailMap;
        proposeWalk = DEFAULT_PROPOSE_WALK;
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

    public void setProposeWalk(Route route) {
        proposeWalk = new ProposeWalk(route);
    }

    public ProposeWalk getProposeWalk(){
        return proposeWalk;
    }
}
