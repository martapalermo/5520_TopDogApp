package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User implements Serializable {

    private String username;
    private String token;
    private String email;
    private String dogName;
    private int walkCounter;
    private ArrayList<Walk> walkList;


    public User () {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String token, String email, String dogName){
        this.username = username;
        this.token = token;
        this.email = email;
        this.dogName = dogName;
        this.walkCounter = 0;
        this.walkList = new ArrayList<>();
    }

    public User getUser(String username, String token){
        return this;
    }

    public String getUsername(){
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDogName() {
        return this.dogName;
    }

    public int getWalkCounter() { return this.walkCounter;}

    public ArrayList<Walk> getWalkList() { return this.walkList;}

    public void setUsername(String username){
        this.username = username;
    }

    public void setToken(String token){
        this.token = token;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setDogName(String dogName){
        this.dogName = dogName;
    }

    public void setWalkList(ArrayList<Walk> walkList) { this.walkList = walkList;}

    public void incrementWalkCounter() {
        walkCounter += 1;
    }

}
