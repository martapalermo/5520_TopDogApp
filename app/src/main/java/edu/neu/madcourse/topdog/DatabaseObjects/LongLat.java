package edu.neu.madcourse.topdog.DatabaseObjects;

public class LongLat{
    private double longitude;
    private double latitude;


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
}
