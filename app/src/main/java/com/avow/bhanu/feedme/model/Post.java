package com.avow.bhanu.feedme.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bhanu on 12/9/17.
 */

public class Post implements Serializable{
    private int id;
    private String image_url;
    private String image;
    private String time;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private Boolean show_info;
    private ArrayList<User> receivers;
    private int receivers_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getShow_info() {
        return show_info;
    }

    public void setShow_info(Boolean show_info) {
        this.show_info = show_info;
    }

    public ArrayList<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(ArrayList<User> receivers) {
        this.receivers = receivers;
    }

    public int getReceivers_count() {
        return receivers_count;
    }

    public void setReceivers_count(int receivers_count) {
        this.receivers_count = receivers_count;
    }
}
