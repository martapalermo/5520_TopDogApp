package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        username = getIntent().getStringExtra(MainActivity.USERKEY);

        TextView welcomeMsg = findViewById(R.id.welcome_msg);
        String displayString = "Welcome " + username + "!";
        welcomeMsg.setText(displayString);

        ImageButton myProfile = findViewById(R.id.myProfile_btn);
        myProfile.setOnClickListener(v -> openMyProfile());

        ImageButton myStats = findViewById(R.id.myStats_btn);
        myStats.setOnClickListener(v -> openMyStats());

        ImageButton myLeaderboard = findViewById(R.id.leaderboard_btn);
        myLeaderboard.setOnClickListener(v -> openMyLeaderboard());

        Button letsWalk = findViewById(R.id.letsWalk_btn);
        letsWalk.setOnClickListener(v -> openWalkTracker());
    }

    public void openWalkTracker() {
        Intent intent = new Intent(this, WalkTracker.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

    public void openMyProfile() {
        Intent intent = new Intent(this, MyProfile.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

    public void openMyStats() {
        Intent intent = new Intent(this, MyStats.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

    public void openMyLeaderboard() {
        Intent intent = new Intent(this, Leaderboard.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}
