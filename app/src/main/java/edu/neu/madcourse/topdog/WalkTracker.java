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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBUserUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LongLat;
import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;


/**
 * A class for managing the walk tracking feature of the app
 */
public class WalkTracker extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars for location functionality
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Location currentLocation = null;

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
        getLocationPermission();

        //Gather all global info needed for this class:
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);

        Button startBtn = findViewById(R.id.startWalk_btn);
        Button stopBtn = findViewById(R.id.stopWalk_btn);

        startBtn.setOnClickListener(v -> onClickStartButton(v, stopBtn));
        stopBtn.setOnClickListener(this::onClickStopButton);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAPACTIVTY", "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }

    private void getDeviceLocation(){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("MAPACTIVTY", "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);
                    }else{
                        Log.d("MAPACTIVTY", "onComplete: current location is null");
                        Toast.makeText(WalkTracker.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("MAPACTIVTY", "getDeviceLocation: SecurityException: " + e.getMessage() );
            Toast.makeText(this, "failed to run getDeviceLocation", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d("MAPACTIVTY", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        Log.d("MAPACTIVTY", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(WalkTracker.this);
    }

    private void getLocationPermission(){
        Log.d("MAPACTIVTY", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MAPACTIVTY", "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d("MAPACTIVTY", "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d("MAPACTIVTY", "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
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

        //Create a Walk object and capture the start time of the walk
        thisWalk = new Walk(new Date().getTime());

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
                    //wait
                } else {
                    LongLat currentLocation = new LongLat(locationLongitude, locationLatitude);
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
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 14, (LocationListener) this);
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

            long endOfWalkTime = new Date().getTime();
            //the finalWalkTime in miliseconds
            //at this point, getWalkDuration() actually returns the start time of the walk,
            //which is captured above when we initialize the Walk object in thisWalk
            double finalWalkDurationMiliSec = endOfWalkTime - thisWalk.getWalkDuration();

            //converts time from milliseconds to seconds then to minutes
            double finalWalkDurationMin = finalWalkDurationMiliSec/1000/60;
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            String finalWalkDurationStr = numberFormat.format(finalWalkDurationMin);
            double finalWalkDurationMinFormat = Double.parseDouble(finalWalkDurationStr);

            String date = new Date().toString();
            thisWalk.setLogDate(date.substring(0,10));
            thisWalk.setWalkDuration(finalWalkDurationMinFormat);//in minutes!

            //EFFECT: calculateFinalDistance updates the "long finalDistance" field of thisWalk
            thisWalk.calculateFinalDistance();


            User userToUpdate = new FetchDBUserUtil().getUser(username);
            userToUpdate.addWalk(thisWalk); //updating user here

            new PutDBInfoUtil().setValue(mDatabase.child(username), userToUpdate);//update database

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
    }

    public void openStatsPage(){
        Intent intent = new Intent(this, MyStats.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}