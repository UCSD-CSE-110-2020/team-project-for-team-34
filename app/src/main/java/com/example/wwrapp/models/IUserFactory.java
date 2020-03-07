package com.example.wwrapp.models;

import com.example.wwrapp.CustomQuery.UserQuery;
import com.example.wwrapp.utils.WWRConstants;

/**
 * Factory class for User objects
 */
public class IUserFactory {

    public static IUser createUser(String type, String name, String email) {
        IUser user = null;
        switch(type) {
            case WWRConstants.MOCK_USER_FACTORY_KEY:
                user = new MockUser(name, email);
                break;

            case WWRConstants.GOOGLE_USER_FACTORY_KEY:
                UserQuery.firstTimeSaveUser(new MockUser(name,email));
                break;

        };
        return user;
    }
}
