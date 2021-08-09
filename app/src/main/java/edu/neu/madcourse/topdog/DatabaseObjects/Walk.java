package edu.neu.madcourse.topdog.DatabaseObjects;

import android.widget.Toast;

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
    public long timeOfWalk; //TODO : method for converting miliseconds into actual date and time
    public long finalDistance;

    public Walk () {
        // Default constructor required for calls to DataSnapshot.getValue(Walk.class)
    }

    public Walk(long dateOfWalk) {
        this.coordinates = new ArrayList<>();
        this.timeOfWalk = dateOfWalk;
        this.finalDistance = 0;
    }

    //adds the next coordinate of the walk to the list of coordinates
    public void addNextCoordinate(LongLat coordinate) {
        this.coordinates.add(coordinate);
    }

    //gathers all the coordinates in the single walk and calculates the DISTANCE covered in the walk
    //EFFECT: returns finalDistance AND updates field this.finalDistance
    public long calculateFinalDistance(){
        long result = 0;
        if (coordinates.size() == 0) return result;
        else {

            //this is here just incase the user exits the app before a second coordinate is retrieved
            if(coordinates.size() >= 2) {

                //looping through coordinates and calculating the result
                for (int i = 1; i < coordinates.size(); i++) {
                    double firstLat = coordinates.get(i - 1).getLatitude();
                    double firstLong = coordinates.get(i - 1).getLongitude();

                    double secondLat = coordinates.get(i).getLatitude();
                    double secondLong = coordinates.get(i).getLongitude();

                    result += calculateDistanceInKilometer(firstLat, firstLong, secondLat, secondLong);
                }
                this.finalDistance = result;
                return result;
            }
            return result;
        }
    }

    private int calculateDistanceInKilometer(double firstLat, double firstLong,
                                            double secondLat, double secondLong) {

        double latDistance = Math.toRadians(secondLat - firstLat);
        double lngDistance = Math.toRadians(secondLong - firstLong);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(firstLat)) * Math.cos(Math.toRadians(secondLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    public List<LongLat> getCoordinates(){
        return this.coordinates;
    }

    public void setCoordinates(ArrayList<LongLat> coordinates){
        this.coordinates = coordinates;
    }

    public long getTimeOfWalk() { return this.timeOfWalk; }

    public void setTimeOfWalk(long timeOfWalk) { this.timeOfWalk = timeOfWalk; }

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
            String timeOfWalk = jsonWalk.get("timeOfWalk").toString();

            returnWalk.setCoordinates(coordinates);
            returnWalk.setTimeOfWalk(Long.parseLong(timeOfWalk));
            returnWalk.setFinalDistance(Integer.parseInt(finalDistance));
        } catch (JSONException e){
            System.out.println("JSON ERROR: WALK -> " + e.toString());
        }
        return returnWalk;
    }
}
