package edu.neu.madcourse.topdog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.GPSPage.GPSActivity;

public class HomePage extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private String username;
    private DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        username = getIntent().getStringExtra(MainActivity.USERKEY);
        mDB = FirebaseDatabase.getInstance().getReference().child("USERS");

        TextView welcomeMsg = findViewById(R.id.welcome_msg);
        String displayString = "Welcome, " + username + "!";
        welcomeMsg.setText(displayString);

        TextView patsDisplay = findViewById(R.id.pats_msg);
        JSONObject jsonUser = new FetchDBInfoUtil().getResults(mDB.child(username));
        User user = User.deserialize(jsonUser);
        String patsString = user.getDogName() + " has " + user.getNumPats() + " pats!";
        patsDisplay.setText(patsString);

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

    @Override
    public void onBackPressed() {
    }

}
