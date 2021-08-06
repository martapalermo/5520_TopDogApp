package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {

    private String username;
    private String token;
    private static int walkCounter;


    public User () {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String token){
        this.username = username;
        this.token = token;
        walkCounter = 0;
    }

    public User getUser(String username, String token){
        return this;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getUsername(){
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public static int getWalkCounter() { return walkCounter;}

    public static void setWalkCounter() {
        walkCounter += 1;
    }

}
