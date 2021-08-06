package edu.neu.madcourse.topdog.DatabaseObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Walk implements Serializable {
    private List<LongLat> distances;
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    public Walk() {
        distances = new ArrayList<>();
    }

    public long LongLatToDistance(){
        long result = 0;
        if (distances.size() == 0) return result;
        else {
            for (int i = 1; i < distances.size(); i++) {

                double firstLat = distances.get(i).getLatitude();
                double secondLat = distances.get(i).getLongitude();
                double firstLong = distances.get(i - 1).getLatitude();
                double secondLong = distances.get(i - 1).getLongitude();

                result += calculateDistanceInKilometer(firstLat, firstLong, secondLat, secondLong);
            }
            return result;
        }
    }


    private int calculateDistanceInKilometer(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    public List<LongLat> getList(){
        return this.distances;
    }


}
