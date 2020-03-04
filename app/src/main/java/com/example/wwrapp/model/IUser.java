package com.example.wwrapp.model;

import java.io.Serializable;

/**
 * Represents a user of the WWR app
 */
public interface IUser extends Serializable {

    public String getName();
    public String getEmail();
    public String getInviteStatus();

}
