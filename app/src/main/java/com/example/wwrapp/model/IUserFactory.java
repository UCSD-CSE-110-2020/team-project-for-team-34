package com.example.wwrapp.model;

import com.example.wwrapp.WWRConstants;
import com.example.wwrapp.models.GoogleUser;
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
                user = new GoogleUser(name, email);
                // TODO: Implement Google User
                break;

        };
        return user;
    }
}
