package edu.neu.madcourse.topdog;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LongLat;
import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

/**
 * A class for managing the walk tracking feature of the app
 */
public class WalkTracker extends AppCompatActivity implements LocationListener {

    private DatabaseReference mDatabase;
    private String username;
    private Walk thisWalk;
    double locationLatitude = 0;
    double locationLongitude = 0;
    LocationManager locationManager;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_tracker);

        //Gather all global info needed for this class:
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);

        thisWalk = new Walk(new Date().getTime());

        Button startBtn = findViewById(R.id.startWalk_btn);
        Button stopBtn = findViewById(R.id.stopWalk_btn);

        startBtn.setOnClickListener(v -> onClickStartButton(v, stopBtn));
        stopBtn.setOnClickListener(this::onClickStopButton);
    }


    //Begins reading user's geographical location, rereads every 5 seconds
    public void onClickStartButton(View start, View stop) {
        //inform user what's going on
        AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Location");
        popup.setMessage("Ready?!");
        popup.setPositiveButton("Start walk", (dialog, which) -> {
            start.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
        });
        popup.show();

        //check for location permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WalkTracker.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        //start the geo location reading
        handler.postDelayed(() -> {
            Handler mHandler = new Handler();
            startRepeatingTask(); //and mStatusChecker displays location to the user
        }, 500); //5 seconds - 5000
    }


    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(mStatusChecker);
    }


    Runnable mStatusChecker = new Runnable() {
        @Override public void run() {
            try {
                getLocation();
                if (locationLatitude == 0 && locationLongitude == 0) {
                    //TODO: Turn this into the loading GIF of the walking dogs
                    Toast.makeText(getApplicationContext(), "Trying to retrieve coordinates.",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Find the LongLat coordinate of user's current location, round 3 dec places
                    //TODO: Find a way to do this without temporarily converting to String ?
                    DecimalFormat numberFormat = new DecimalFormat("#.000");
                    String lonStr = numberFormat.format(locationLongitude);
                    String latStr = numberFormat.format(locationLatitude);
                    double lon = Double.parseDouble(lonStr);
                    double lat = Double.parseDouble(latStr);

                    LongLat currentLocation = new LongLat(lon, lat);

                    //Add current location to the collection of coordinates visited within this walk
                    thisWalk.addNextCoordinate(currentLocation);
                }
            }
            finally {
                handler.postDelayed(mStatusChecker, 3000); //3000
            }
        }
    };

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.reque
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 2, (LocationListener) this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //these are of type double
        locationLatitude = location.getLatitude();
        locationLongitude = location.getLongitude();
    }


    @Override public void onDestroy() {
        super.onDestroy(); stopRepeatingTask(); }


    @Override public void onProviderDisabled(String provider) {
        Toast.makeText(WalkTracker.this, "Please Enable GPS",
                Toast.LENGTH_SHORT).show();
    }

    //Upon stopping the walk:
    // - calculate the total distance of the walk
    // - jump off the main thread and go fetch the User's database records for updating
    // - deserialize from JSON to User object
    // - update that User object to reflect the changes (the new walk)
    // - Put that updatedUser back into the database
    public void onClickStopButton(View v) {
        AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Walk Complete");
        popup.setMessage("Are you sure?");
        popup.setNegativeButton("Resume", (dialog, which) -> {

        });
        popup.setPositiveButton("Done", (dialog, which) -> {
            stopRepeatingTask();
            mStatusChecker = null;
            //EFFECT: calculateFinalDistance updates the "long finalDistance" field of thisWalk
            thisWalk.calculateFinalDistance();
            DatabaseReference user = mDatabase.child(username);

            //Use utility classes to jump off the main thread when fetching and putting info in the db
            JSONObject jsonUser = new FetchDBInfoUtil().getResults(user);
            User userToUpdate = User.deserialize(jsonUser);
            userToUpdate.addWalk(thisWalk); //updating user here
            new PutDBInfoUtil().setValue(user, userToUpdate);//update database off main thread

            //move to stats
            openStatsPage();

        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
        finish();
    }

    public void openStatsPage(){
        Intent intent = new Intent(this, MyStats.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}