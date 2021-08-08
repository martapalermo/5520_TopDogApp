package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class LongLat implements Serializable {
    private double longitude;
    private double latitude;

    public LongLat() {
        // Default constructor required for calls to DataSnapshot.getValue(LongLat.class)
    }

    public LongLat(double lon, double lat){
        latitude = lat;
        longitude = lon;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude){ this.longitude = longitude; }

    public void setLatitude(double latitude){ this.latitude = latitude; }

    public static LongLat deserialize(JSONObject jsonWalk) {
        LongLat returnCoordinate = new LongLat();
        try{
            String longitude = jsonWalk.get("longitude").toString();
            String latitude = jsonWalk.get("latitude").toString();

            returnCoordinate.setLongitude(Double.parseDouble(longitude));
            returnCoordinate.setLatitude(Double.parseDouble(latitude));
        } catch (JSONException e){
            System.out.println("JSON ERROR: LONGLAT -> " + e.toString());
        }
        return returnCoordinate;
    }
}
