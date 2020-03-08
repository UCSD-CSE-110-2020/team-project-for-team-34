package com.example.wwrapp.models;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

public class Team {
    private String mTeamName;
    private Map<IUser, Boolean> mMembers = new HashMap<>();

    public String getTeamName() {
        return mTeamName;
    }

    public Boolean getMemberStatus(IUser user) {
        return mMembers.get(user);
    }

    public void setTeamName(String newTeamName) {
        mTeamName = newTeamName;
    }

    public void addMember(IUser user, Boolean status) {
        mMembers.put(user, status);
    }

    public void updateMemberStatus(IUser user, Boolean status) {
        mMembers.put(user, status);
    }

    public Boolean isMember(IUser user) {
        return mMembers.containsKey(user);
    }
}
