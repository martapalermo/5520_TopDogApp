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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.topdog.DatabaseObjects.LongLat;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

/**
 * Notes for team: We should find a way to get the coordinates
 * of the start position and then compare them to the end position
 * and see if we can use some fancy algo to figure out how far the user walked.
 */

public class WalkTracker extends AppCompatActivity implements LocationListener {

    private String username;
    private DatabaseReference mDatabase;
    private ArrayList<LongLat> LocationsList = new ArrayList<>();
    private int walkCounter;

    LocationManager locationManager;
    Handler handler;
    String locationText = "";
    String locationLatitude = "";
    String locationLongitude = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_tracker);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);
        walkCounter = Walk.getWalkCounter();


                AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Location");
        popup.setMessage("Location will update every few seconds");

        popup.setPositiveButton("Start walk", (dialog, which) -> {

        });
        popup.show();

        handler = new Handler();
        handler.postDelayed(() -> {
            Handler mHandler = new Handler();
            startRepeatingTask();
        }, 5000); //5 seconds

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }

    @Override public void onDestroy() {
        super.onDestroy(); stopRepeatingTask(); }


    Runnable mStatusChecker = new Runnable() {
        @Override public void run() {
            final EditText yourlat = (EditText) findViewById(R.id.latitude);
            final EditText yourlong = (EditText) findViewById(R.id.longitude);
            // TODO: add these to the database to calculate the distance

            try { getLocation(); //this function can change value of mInterval.
                if (locationText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Trying to retrieve coordinates.",
                            Toast.LENGTH_LONG).show();
                } else {
                    yourlat.setText(locationLatitude);
                    yourlong.setText(locationLongitude);

                    double lon = Double.parseDouble(locationLongitude);
                    double lat = Double.parseDouble(locationLatitude);


                    LongLat currentLocation = new LongLat(lon, lat);

                    LocationsList.add(currentLocation);

                    DatabaseReference Loc = mDatabase.child(username).child("walks")
                                                    .child("Walk " + walkCounter).push();

                    Loc.setValue(currentLocation);



                }
            }
            finally {
                handler.postDelayed(mStatusChecker, 3000);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(mStatusChecker);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 5, (LocationListener) this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationText = location.getLatitude() + "," + location.getLongitude();
        locationLatitude = location.getLatitude() + "";
        locationLongitude = location.getLongitude() + "";
    }

    @Override public void onProviderDisabled(String provider) {
        Toast.makeText(WalkTracker.this, "Please Enable GPS",
                Toast.LENGTH_SHORT).show();
    }

    // TODO: Figure out how to end the tracker safely and then display distance to user
    public void onClick(View view) {

        stopRepeatingTask();
        mStatusChecker = null;
        Walk.setWalkCounter();

        AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Walk Complete");
        popup.setMessage("Walk is over! Give your dog a pat and click 'calculate' to see distance of walk.");
        popup.setPositiveButton("CALCULATE", (dialog, which) -> {
            Intent intent = new Intent(this, MyStats.class);
            intent.putExtra(MainActivity.USERKEY, username);
            startActivity(intent);
        });
        popup.show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}