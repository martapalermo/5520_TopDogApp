package edu.neu.madcourse.topdog;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Notes for team: We should find a way to get the coordinates
 * of the start position and then compare them to the end position
 * and see if we can use some fancy algo to figure out how far the user walked.
 */

public class WalkTracker extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    Handler handler;
    String locationText = "";
    String locationLatitude = "";
    String locationLongitude = "";
    Button stopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content to the XML file
        setContentView(R.layout.activity_walk_tracker);
        stopButton.findViewById(R.id.stop_btn);
        // TODO: add onClick so that the walk has ended and the tracking will stop - open new page

        AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Location");
        popup.setMessage("Location will update every few seconds");

        popup.setPositiveButton("Start run", (dialog, which) -> {

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
                Toast.LENGTH_SHORT).show(); }

}