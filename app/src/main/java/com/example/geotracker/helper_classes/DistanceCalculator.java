package com.example.geotracker.helper_classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.view_model.MainViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Helper class to complete distance related calculations
public class DistanceCalculator {
    public static final String LOG_TAG = DistanceCalculator.class.getSimpleName(); // Log tag

    // Private constructor to prevent instantiation
    private DistanceCalculator() {
        // Empty constructor
    }

    // Calculate the distance between two locations
    public static  float calculateDistance(List<LocationEntity> locationEntities) {
        float distance = 0.0f; // The distance

        // Ensure that locationEntities is not null and has at least two locations
        if (locationEntities != null && locationEntities.size() > 1) {
            // Sort the locationEntities by date (year, month, day)
            Collections.sort(locationEntities, new Comparator<LocationEntity>() {
                @Override
                public int compare(LocationEntity entity1, LocationEntity entity2) {
                    // Compare by year first
                    int yearDiff = entity1.getYear() - entity2.getYear();
                    if (yearDiff != 0) {
                        return yearDiff;
                    }

                    // Compare by month if years are the same
                    int monthDiff = entity1.getMonth() - entity2.getMonth();
                    if (monthDiff != 0) {
                        return monthDiff;
                    }

                    // Compare by day if years and months are the same
                    return entity1.getDay() - entity2.getDay();
                }
            });

            // Sort the locations by timestamp
            Collections.sort(locationEntities, new Comparator<LocationEntity>() {
                @Override
                public int compare(LocationEntity entity1, LocationEntity entity2) {
                    return (int) (entity1.getTimestamp() - entity2.getTimestamp());
                }
            });

        }

        // Calculate the distance between each location
        if (locationEntities != null && locationEntities.size() > 1) {
            for(int i = 0; i < locationEntities.size() - 1; i++) { // For each location

                if (!locationEntities.get(i).getLastLocation()){
                    // Create two location objects for calculating distance
                    Location location1 = new Location("location1");
                    location1.setLatitude(locationEntities.get(i).getLatitude());
                    location1.setLongitude(locationEntities.get(i).getLongitude());
                    Location location2 = new Location("location2");
                    location2.setLatitude(locationEntities.get(i + 1).getLatitude());
                    location2.setLongitude(locationEntities.get(i + 1).getLongitude());
                    // Add the distance between the two locations to the total distance
                    distance += location1.distanceTo(location2);
                }
            }
        }
        // Return the distance
        return distance;
    }

    // Calculate the distance between two locations using latitude and longitude
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Create array to hold the results
        float[] results = new float[1];

        // Calculate the distance between the two locations
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);

        // Return the distance
        return results[0];
    }

    // Convert the distance to the appropriate unit of measurement
    public static float convertDistance(float distance, int measureOption, float height, String selectedMeasurement) {
        // Convert the distance to the appropriate unit of measurement
        if (selectedMeasurement.equals("metric")) { // Metric
            // Convert the distance to the appropriate unit of measurement
            switch (measureOption) {
                case 0: // Steps
                    distance = DistanceCalculator.distanceToSteps(distance, height);
                    break;
                case 1: // Meters
                    distance = DistanceCalculator.distanceToMeters(distance);
                    break;
                case 2: // Kilometers
                    distance = DistanceCalculator.distanceToKilometers(distance);
                    break;
                default:
                    break;
            }
            return distance;
        } else { // Imperial
            // Convert the distance to the appropriate unit of measurement
            switch (measureOption) {
                case 0: // Steps
                    distance = DistanceCalculator.distanceToSteps(distance, height);
                    break;
                case 1: //  Yards
                    distance = DistanceCalculator.distanceToYards(distance);
                    break;
                case 2: // Miles
                    distance = DistanceCalculator.distanceToMiles(distance);
                    break;
                default:
                    break;
            }
            // Return the distance
            return distance;
        }
    }

    // Convert the distance to miles
    private static float distanceToMiles(float distance) {
        Log.d(LOG_TAG, "distanceToMiles called");
        float miles = distance / 1609.344f;
        return Math.round(miles * 10.0f) / 10.0f;
    }

    // Convert the distance to yards
    private static float distanceToYards(float distance) {
        Log.d(LOG_TAG, "distanceToYards called");
        float yards = distance / 0.9144f;
        return Math.round(yards * 10.0f) / 10.0f;
    }

    // Convert the distance to steps
    private static float distanceToSteps(float distance, float height) {
        // This is a rough average based on general observations and may not be accurate for all individuals.
        float strideLengthFactor = 0.415f;

        // Calculate steps by dividing the distance by the estimated stride length
        return Math.round(distance / (height * strideLengthFactor));
    }

    // Convert the distance to meters
    private static float distanceToMeters(float distance) {
        return Math.round((distance)* 10.0) / 10.0f;
    }

    // Convert the distance to kilometers
    private static float distanceToKilometers(float distance) {
        return Math.round((distance / 1000.0) * 10.0) / 10.0f;
    }
}
