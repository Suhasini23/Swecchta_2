package com.example.shabbir.swecchta_2;

/**
 * Created by shabbir on 3/2/2018.
 */
class User {

    public String username;
    public String email;
    public String udi;
    public String urlToProfileImage;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUdi() {
        return udi;
    }

    public String getUrlToProfileImage() {
        return urlToProfileImage;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String uid,String urlToProfileImage) {
        this.username = username;
        this.email = email;
        this.udi = uid;
        this.urlToProfileImage = urlToProfileImage;
//        this.likeTo = likeTo;
    }

}