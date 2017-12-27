package com.avow.bhanu.feedme.model;

import java.io.Serializable;

/**
 * Created by bhanu on 22/8/17.
 */

public class User implements Serializable{
    String name;
    String username;
    String image;
    String status;



    public User(String name, String username, String image, String status) {
        this.name = name;
        this.username = username;
        this.image = image;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
