package com.example.mychat.Model;

public class User {
    private String id;
    private String username;
    private String imageUrl;
    private String status;

    User(){}

    public User(String id, String username, String imageUrl) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}