package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        Button letsWalk = findViewById(R.id.letsWalk_btn);
        letsWalk.setOnClickListener(v -> openWalkTracker());

        ImageButton myProfile = findViewById(R.id.myProfile_btn);
        myProfile.setOnClickListener(v -> openMyProfile());

        ImageButton myStats = findViewById(R.id.myStats_btn);
        myStats.setOnClickListener(v -> openMyStats());

        ImageButton myLeaderboard = findViewById(R.id.leaderboard_btn);
        myLeaderboard.setOnClickListener(v -> openMyLeaderboard());
    }

    public void openWalkTracker() {
        Intent intent = new Intent(this, WalkTracker.class);
        startActivity(intent);
    }

    public void openMyProfile() {
        Intent intent = new Intent(this, MyProfile.class);
        startActivity(intent);
    }

    public void openMyStats() {
        Intent intent = new Intent(this, MyStats.class);
        startActivity(intent);
    }

    public void openMyLeaderboard() {
        Intent intent = new Intent(this, Leaderboard.class);
        startActivity(intent);
    }

}
