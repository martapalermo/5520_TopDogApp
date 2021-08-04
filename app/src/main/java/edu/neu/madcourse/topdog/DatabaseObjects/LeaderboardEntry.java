package edu.neu.madcourse.topdog.DatabaseObjects;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class LeaderboardEntry {

    private String username;
    private long numberWalks;


    public LeaderboardEntry(String username, long numberWalks){
        this.username = username;
        this.numberWalks = numberWalks;
    }

    public String getUsername(){
        return this.username;
    }

    public long getNumberWalks() {
        return this.numberWalks = numberWalks;
    }

}