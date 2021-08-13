package edu.neu.madcourse.topdog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import edu.neu.madcourse.topdog.GPSPage.GPSActivity;

public class HomePage extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    String API_KEY = "AIzaSyA_4czHi0sxfMnlOO3_icmMe8RpeudtjW8";
    private Button mapButton;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        username = getIntent().getStringExtra(MainActivity.USERKEY);

        TextView welcomeMsg = findViewById(R.id.welcome_msg);
        String displayString = "Welcome, " + username + "!";
        welcomeMsg.setText(displayString);

        ImageButton myProfile = findViewById(R.id.myProfile_btn);
        myProfile.setOnClickListener(v -> openMyProfile());

        ImageButton myStats = findViewById(R.id.myStats_btn);
        myStats.setOnClickListener(v -> openMyStats());

        ImageButton myLeaderboard = findViewById(R.id.leaderboard_btn);
        myLeaderboard.setOnClickListener(v ->openMyLeaderboard());

        ImageButton letsWalk = findViewById(R.id.letsWalk_btn);
        letsWalk.setOnClickListener(v -> openWalkTracker());

//        Button gpsTestingButton = findViewById(R.id.gpsTestingButton);
//        gpsTestingButton.setOnClickListener(v-> openGPS());
    }

//    public void openDialog() {
//        CustomDialog dialog = new CustomDialog();
//        dialog.show(getSupportFragmentManager(), "custom dialog");
//    }

    //on rotation changes we have to redo the oncreate activity.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int newOrientation = newConfig.orientation;

        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_user_homepage);
            username = getIntent().getStringExtra(MainActivity.USERKEY);

            TextView welcomeMsg = findViewById(R.id.welcome_msg);
            String displayString = "Welcome, " + username + "!";
            welcomeMsg.setText(displayString);

            ImageButton myProfile = findViewById(R.id.myProfile_btn);
            myProfile.setOnClickListener(v -> openMyProfile());

            ImageButton myStats = findViewById(R.id.myStats_btn);
            myStats.setOnClickListener(v -> openMyStats());

            ImageButton myLeaderboard = findViewById(R.id.leaderboard_btn);
            myLeaderboard.setOnClickListener(v ->openMyLeaderboard());

            ImageButton letsWalk = findViewById(R.id.letsWalk_btn);
            letsWalk.setOnClickListener(v -> openWalkTracker());
            // Do certain things when the user has switched to landscape.
        }
        if(newOrientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_user_homepage);
            username = getIntent().getStringExtra(MainActivity.USERKEY);

            TextView welcomeMsg = findViewById(R.id.welcome_msg);
            String displayString = "Welcome, " + username + "!";
            welcomeMsg.setText(displayString);

            ImageButton myProfile = findViewById(R.id.myProfile_btn);
            myProfile.setOnClickListener(v -> openMyProfile());

            ImageButton myStats = findViewById(R.id.myStats_btn);
            myStats.setOnClickListener(v -> openMyStats());

            ImageButton myLeaderboard = findViewById(R.id.leaderboard_btn);
            myLeaderboard.setOnClickListener(v ->openMyLeaderboard());

            ImageButton letsWalk = findViewById(R.id.letsWalk_btn);
            letsWalk.setOnClickListener(v -> openWalkTracker());
        }
    }

    public void openGPS(){
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
    }

    public void openWalkTracker() {
        Intent intent = new Intent(this, WalkTracker.class);
        intent.putExtra(MainActivity.USERKEY, username);
        if (isServicesUpToDate()){
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Error: GPS/Google Service not up-to-date", Toast.LENGTH_SHORT).show();
        }

    }

    //checks if GPS services are up-to-date prior to opening walk tracker
    public boolean isServicesUpToDate(){
        Log.d("GPS ACTIVITY", "isServicesUpToDate: ");
        int avaialble = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(avaialble == ConnectionResult.SUCCESS){
            Log.d("GPS ACTIVITY", "isServicesUpToDate: Google play services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaialble)){
            //an error occured like they have the wrong version but is fixablke
            Log.d("GPS ACTIVITY", "isServicesOK: An error occured but fixable");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(this, avaialble, ERROR_DIALOG_REQUEST);
        }else{
            Toast.makeText(this, "Can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;

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
