package edu.neu.madcourse.topdog.GPSPage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import edu.neu.madcourse.topdog.*;

import static android.content.ContentValues.TAG;

public class GPSActivity extends AppCompatActivity {

    private static final String TAG = "GPSActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    String API_KEY =  "AIzaSyA_4czHi0sxfMnlOO3_icmMe8RpeudtjW8";
    private Button mapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsactivity);
        if (isServicesUpToDate()){
            init();
        }
    }

    //The initializing function
    private void init(){
        mapButton = findViewById(R.id.GPSButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GPSActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
    
    public boolean isServicesUpToDate(){
        Log.d(TAG, "isServicesUpToDate: ");
        int avaialble = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(avaialble == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesUpToDate: Google play services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaialble)){
            //an error occured like they have the wrong version but is fixablke
            Log.d(TAG, "isServicesOK: An error occured but fixable");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(this, avaialble, ERROR_DIALOG_REQUEST);
        }else{
            Toast.makeText(this, "Can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }
}