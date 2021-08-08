package edu.neu.madcourse.topdog.DatabaseObjects;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@IgnoreExtraProperties
public class User implements Serializable {

    public String username;
    public String token;
    public String email;
    public String dogName;
    public int dogAge;
    public ArrayList<Walk> walkList;

    public User () {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String token, String email, String dogName){
        this.username = username;
        this.token = token;
        this.email = email;
        this.dogName = dogName;
        this.dogAge = 0;
        this.walkList = new ArrayList<>();

    }

    public void addWalk(Walk walk) {
        this.walkList.add(walk);
    }

    public static User deserialize(JSONObject jsonUser) {
        User returnUser = new User();
        try{
            String username = jsonUser.get("username").toString();
            String token = jsonUser.get("token").toString();
            String email = jsonUser.get("email").toString();
            String dogName = jsonUser.get("dogName").toString();
            String dogAge = jsonUser.get("dogAge").toString();

            ArrayList<Walk> walkList = new ArrayList<>();
            if (jsonUser.has("walkList")) {
                JSONArray walkListJSON = jsonUser.getJSONArray("walkList");
                for (int i=0; i<walkListJSON.length(); i++) {
                    Walk aWalk = Walk.deserialize(walkListJSON.getJSONObject(i));
                    walkList.add(aWalk);
                }
            }
            returnUser.setUsername(username);
            returnUser.setToken(token);
            returnUser.setEmail(email);
            returnUser.setDogName(dogName);
            returnUser.setDogAge(Integer.parseInt(dogAge));
            returnUser.setWalkList(walkList);

        } catch (JSONException e) {
            System.out.println("JSON ERROR: USER ->" + e.toString());
        }
        return returnUser;
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

    public int getDogAge() {
        return this.dogAge;
    }

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

    public void setDogAge(int age) { this.dogAge = age; }

    public void setWalkList(ArrayList<Walk> walkList) { this.walkList = walkList; }
}
