package com.example.wwrapp.models;

import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RandomColorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GoogleUser extends AbstractUser {

    private static final List<String> INVITEES_DEFAULT = new ArrayList<>();
    private static final List<Route> ROUTES_DEFAULT = new ArrayList<>();
    private static final String STRING_DEFAULT = "";

    private String name;
    private String email;
    private String inviter;
    private String teamName;

    private String status;
    private int color;

    public GoogleUser() {
    }

    public GoogleUser(String name, String email) {

        this.name = name;
        this.email = email;
        color = RandomColorGenerator.generateRandomNum();
        status = FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED;
        inviter = STRING_DEFAULT;
        teamName = STRING_DEFAULT;
    }


        @Override
        public boolean equals (Object o){
            if ((o instanceof AbstractUser)) {
                AbstractUser user = (AbstractUser) o;
                return name.equals(user.getName());
            } else {
                return false;
            }
        }
    }


