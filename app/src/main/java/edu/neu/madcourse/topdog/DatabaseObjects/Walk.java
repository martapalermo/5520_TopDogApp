package edu.neu.madcourse.topdog.DatabaseObjects;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single walk a User can go on with their pet.
 * - coordinates is a list of LongLat objects that represents the longitude and latitude coordinate
 *   points the user visited while on the walk (updated every 5 seconds)
 * - timeOfWalk is a long representing the date and time in miliseconds
 * - finalDistance can be updated once the walk is complete using calculateFinalDistance
 */
@IgnoreExtraProperties
public class Walk implements Serializable {

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    //coordinates is a list of LongLat (a longitude and latitude pair) that represent physical
    //geographical locations that were visited during the walk
    public ArrayList<LongLat> coordinates;
    public double walkDuration;
    public long finalDistance;

    public String logDate;

    public Walk () {
        // Default constructor required for calls to DataSnapshot.getValue(Walk.class)
    }

    public Walk(double startOfWalk) {
        this.coordinates = new ArrayList<>();
        this.walkDuration = startOfWalk;
        this.finalDistance = 0;
    }

    public Walk(long walkDistance, double walkDuration, String logDate) {
        this.walkDuration = walkDuration;
        this.finalDistance = walkDistance;
        this.logDate = logDate;
    }


    //function to get distance between two locations (LatLng)
    public static float distanceBetween(LongLat first, LongLat second) {
        float[] distance = new float[1];
        Location.distanceBetween(first.getLatitude(), first.getLongitude(), second.getLatitude(), second.getLongitude(), distance);
        return distance[0];
    }

    public long calculateFinalDistance(){
        long result = 0;
        if (coordinates.size() == 0) return result;
        else {
            if(coordinates.size() >= 2) {
                //looping through coordinates and calculating the result
                for (int i = 1; i < coordinates.size(); i++) {
                    result += distanceBetween(coordinates.get(i), coordinates.get(i-1));
                }
                this.finalDistance = result;
                return result;
            }
            return result;
        }
    }

    //adds the next coordinate of the walk to the list of coordinates
    public void addNextCoordinate(LongLat coordinate) {
        this.coordinates.add(coordinate);
    }

    public String getLogDate() { return this.logDate; }

    public void setLogDate(String logDate) { this.logDate = logDate; }

    public List<LongLat> getCoordinates(){
        return this.coordinates;
    }

    public void setCoordinates(ArrayList<LongLat> coordinates){
        this.coordinates = coordinates;
    }

    public double getWalkDuration() { return this.walkDuration; }

    public void setWalkDuration(double walkDuration) { this.walkDuration = walkDuration;}

    public long getFinalDistance(){ return this.finalDistance; }

    public void setFinalDistance(long finalDistance) {this.finalDistance = finalDistance; }

    public static Walk deserialize(JSONObject jsonWalk) {
        Walk returnWalk = new Walk();
        try{
            ArrayList<LongLat> coordinates = new ArrayList<>();
            if (jsonWalk.has("coordinates")) {
                JSONArray CoordListJSON = jsonWalk.getJSONArray("coordinates");
                for (int i=0; i<CoordListJSON.length(); i++) {
                    LongLat aCoord = LongLat.deserialize(CoordListJSON.getJSONObject(i));
                    coordinates.add(aCoord);
                }
            }
            String finalDistance = jsonWalk.get("finalDistance").toString();
            String walkDuration = jsonWalk.get("walkDuration").toString();
            String logDate = jsonWalk.get("logDate").toString();

            returnWalk.setLogDate(logDate);
            returnWalk.setCoordinates(coordinates);
            returnWalk.setWalkDuration(Double.parseDouble(walkDuration));
            returnWalk.setFinalDistance(Integer.parseInt(finalDistance));
        } catch (JSONException e){
            System.out.println("JSON ERROR: WALK -> " + e.toString());
        }
        return returnWalk;
    }
}
