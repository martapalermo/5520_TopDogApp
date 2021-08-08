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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchInfoFromDatabse;
import edu.neu.madcourse.topdog.DatabaseObjects.LongLat;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

//Class for tracking walks the user goes on with their dog
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

        LocalDateTime now = LocalDateTime.now();
        thisWalk = new Walk(now);

        Button startBtn = findViewById(R.id.startWalk_btn);
        startBtn.setOnClickListener(this::onClickStartButton);

        Button stopBtn = findViewById(R.id.stopWalk_btn);
        stopBtn.setOnClickListener(this::onClickStopButton);
    }


    //Begins reading user's geographical location, rereads every 5 seconds
    public void onClickStartButton(View v) {
        //inform user what's going on
        AlertDialog.Builder popup = new AlertDialog.Builder(WalkTracker.this);
        popup.setTitle("Location");
        popup.setMessage("Ready?!");
        popup.setPositiveButton("Start walk", (dialog, which) -> { });
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
        }, 5000); //5 seconds - 5000
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

                    //Add current location to the collection of coordinate visited within this walk
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 5, (LocationListener) this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
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
    // - jump off the main thread and go fetch the User's database records for oupdating
    // - deserialize from JSON to USer object
    // - update database with new User object
    public void onClickStopButton(View view) {
        stopRepeatingTask();
        mStatusChecker = null;
        long distance = thisWalk.calculateFinalDistance();
        DatabaseReference user = mDatabase.child(username);

        //Jump off main thread to worker thread//
        String url = user.toString() + ".json";
        FetchInfoFromDatabse fetchRequest = new FetchInfoFromDatabse(url);
        new Thread(fetchRequest).start();
        try {
            //to avoid a data race TODO: come up with a better solution than this
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        JSONObject results = fetchRequest.getResults();

        //EFFECT: updatedUser is set with all fields set to information from the database
        User updatedUser = User.deserialize(results);
        user.setValue(updatedUser);

        /////////////////////////////////////////
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
        finish();
    }

}