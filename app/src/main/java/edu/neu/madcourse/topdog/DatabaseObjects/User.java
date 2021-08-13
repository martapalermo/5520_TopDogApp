package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * NOTE: we should find a way for the setProfilePicUri to grab the contentURI
 * from the myProfile page and everytime set it to whatever image is selected.
 */

@IgnoreExtraProperties
public class User implements Serializable {

    public String username;
    public String token;
    public String email;
    public String dogName;
    public String dogAge;
    public ArrayList<Walk> walkList;
    public String profilePicUri;
    public int numPats;

    public User () {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String token, String email, String dogName, String dogAge){
        this.username = username; //dog name
        this.token = token;
        this.email = email;
        this.dogName = dogName; // user's name
        this.dogAge = dogAge;
        this.walkList = new ArrayList<>();
        profilePicUri = "";
        numPats = 0;
    }

    public void addWalk(Walk walk) {
        this.walkList.add(walk);
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

    public String getDogAge() {
        return this.dogAge;
    }

    public ArrayList<Walk> getWalkList() { return this.walkList;}

    public String getProfilePicUri() { return this.profilePicUri; }

    public int getNumPats() {
        return this.numPats;
    }

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

    public void setDogAge(String age) { this.dogAge = age; }

    public void setWalkList(ArrayList<Walk> walkList) { this.walkList = walkList; }

    public void setNumPats(int numPats) {
        this.numPats = numPats;
    }

    public void setProfilePicUri(String profilePic) {
        this.profilePicUri = profilePic;
    }

    public static User deserialize(JSONObject jsonUser) {
        User returnUser = new User();

        try{
            String username = jsonUser.get("username").toString();
            String token = jsonUser.get("token").toString();
            String email = jsonUser.get("email").toString();
            String dogName = jsonUser.get("dogName").toString();
            String dogAge = jsonUser.get("dogAge").toString();
            String profilePicUri = jsonUser.get("profilePicUri").toString();
            int pats = Integer.parseInt(jsonUser.get("numPats").toString());

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
            returnUser.setDogAge(dogAge);
            returnUser.setWalkList(walkList);
            returnUser.setProfilePicUri(profilePicUri);
            returnUser.setNumPats(pats);

        } catch (JSONException e) {
            System.out.println("JSON ERROR: USER ->" + e.toString());
        }
        return returnUser;
    }
}
