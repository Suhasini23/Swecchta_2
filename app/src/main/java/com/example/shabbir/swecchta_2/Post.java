package com.example.shabbir.swecchta_2;

/**
 * Created by shabbir on 3/3/2018.
 */

public class Post {

    public String postId;
    public String author;
    public String postImageUrl;
    public String description;
    public String userID;
    public String location;

    public Post(String postId, String author, String postImageUrl, String description, String userID, String location) {
        this.postId = postId;
        this.author = author;
        this.postImageUrl = postImageUrl;
        this.description = description;
        this.userID = userID;
        this.location = location;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostId() {

        return postId;
    }

    public String getAuthor() {
        return author;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getLocation() {
        return location;
    }




}
