package com.example.mychat;

public class User {

    private String name, id, imageURL,status;

    public User() {
    }


    public User(String name, String id, String imageURL, String status) {
        this.name = name;
        this.id = id;
        this.imageURL = imageURL;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
