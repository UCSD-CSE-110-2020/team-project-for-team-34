package com.example.wwrapp.models;

import com.example.wwrapp.utils.WWRConstants;

/**
 * Factory class for User objects
 */
public abstract class AbstractUserFactory {

    public static AbstractUser createUser(String type, String name, String email, String teamName,
                                          String teamStatus) {
        AbstractUser user = null;
        switch(type) {
            case WWRConstants.GOOGLE_USER_FACTORY_KEY:
                user = new GoogleUser(name,email);
                break;
            case WWRConstants.WWR_USER_FACTORY_KEY:
                user = new WWRUser(name, email, teamName, teamStatus);
                break;

        };
        return user;
    }
}
