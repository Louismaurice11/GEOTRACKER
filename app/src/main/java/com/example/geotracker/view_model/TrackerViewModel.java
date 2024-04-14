package com.example.geotracker.view_model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.geotracker.helper_classes.DistanceCalculator;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ViewModel for the tracker activity
public class TrackerViewModel extends AndroidViewModel {
    public static final String LOG_TAG = TrackerViewModel.class.getSimpleName(); // Log tag

    // String constants
    public static final String STEPS = " steps";
    public static final String METERS = " m";
    public static final String KILOMETERS = " km";
    public static final String YARDS = " yd";
    public static final String MILES = " mi";


    private int measureOption = 0; // 0 = steps, 1 = meters, 2 = km

    private LocationRepository repository; // Repository for the database

    // Live data
    private MutableLiveData<Float> dayDis = new MutableLiveData<>(0.0f);
    private MutableLiveData<Float> monthDis = new MutableLiveData<>(0.0f);
    private MutableLiveData<Float> yearDis = new MutableLiveData<>(0.0f);
    private MutableLiveData<Float> averageDistance = new MutableLiveData<>(0.0f);
    private MutableLiveData<String> distanceUnit = new MutableLiveData<>(STEPS);
    private LiveData<List<LocationEntity>> dayLocations;
    private LiveData<List<LocationEntity>> monthLocations;
    private LiveData<List<LocationEntity>> yearLocations;

    private final String selectedMeasurement; // The selected measurement system
    private final float userHeight; // The user's height


    // Constructor
    public TrackerViewModel(Application application) {
        super(application);

        // Set up repository
        repository = new LocationRepository(application);

        // Get the selected measurement system and user height from preferences
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.example.geotracker", MODE_PRIVATE);
        selectedMeasurement = sharedPreferences.getString("measurement_system", "metric");
        userHeight = sharedPreferences.getFloat("user_height", 1.7f);

        // Set current date
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        // Calculate distance traveled today
        dayLocations = repository.getLocationsFromToday(day, month, year);
        Log.d(LOG_TAG, "year: " + year + ", month: " + month + ", day: " + day);
        dayLocations.observeForever(locationEntities -> {
            if (locationEntities != null && !locationEntities.isEmpty()) {
                float distance = DistanceCalculator.calculateDistance(locationEntities);
                dayDis.postValue(DistanceCalculator.convertDistance(distance, measureOption, userHeight, selectedMeasurement));
            }
        });

        // Calculate distance traveled this month
        monthLocations = repository.getLocationsFromLastMonth(month, year);
        monthLocations.observeForever(locationEntities -> {
            if (locationEntities != null && !locationEntities.isEmpty()) {
                float distance = DistanceCalculator.calculateDistance(locationEntities);
                monthDis.postValue(DistanceCalculator.convertDistance(distance, measureOption, userHeight, selectedMeasurement));
            }
        });

        // Calculate distance traveled this year
        yearLocations = repository.getLocationsFromLastYear(year);
        yearLocations.observeForever(locationEntities -> {
            if (locationEntities != null && !locationEntities.isEmpty()) {
                float distance = DistanceCalculator.calculateDistance(locationEntities);
                yearDis.postValue(DistanceCalculator.convertDistance(distance, measureOption, userHeight, selectedMeasurement));

                // Calculate average distance traveled per day for the entire year
                calculateAverageDistance(locationEntities);
            }
        });
    }

    // Getters and setters
    public MutableLiveData<String> getDistanceUnit() {
        return distanceUnit;
    }
    public MutableLiveData<Float> getDayDis() {
        return dayDis;
    }
    public MutableLiveData<Float> getMonthDis() {
        return monthDis;
    }
    public MutableLiveData<Float> getYearDis() {
        return yearDis;
    }
    public int getMeasureOption() {
        return measureOption;
    }
    public void setMeasureOption(int measureOption) {
        // Set the distance unit based on the measure option
        switch (measureOption) {
            case 0:
                distanceUnit.postValue(STEPS);
                break;
            case 1:
                if (selectedMeasurement.equals("metric")) {
                    distanceUnit.postValue(METERS);
                } else {
                    distanceUnit.postValue(YARDS);
                }
                break;
            case 2:
                if (selectedMeasurement.equals("metric")) {
                    distanceUnit.postValue(KILOMETERS);
                } else {
                    distanceUnit.postValue(MILES);
                }
                break;
        }

        // Set the measure option
        this.measureOption = measureOption;
    }
    public MutableLiveData<Float> getAverageDistance() {
        return averageDistance;
    }

    // Update the UI with the latest data
    public void updateUI(){
        dayDis.postValue(DistanceCalculator.convertDistance(DistanceCalculator.calculateDistance(dayLocations.getValue()), measureOption, userHeight, selectedMeasurement));
        monthDis.postValue(DistanceCalculator.convertDistance(DistanceCalculator.calculateDistance(monthLocations.getValue()), measureOption, userHeight, selectedMeasurement));
        yearDis.postValue(DistanceCalculator.convertDistance(DistanceCalculator.calculateDistance(yearLocations.getValue()), measureOption, userHeight, selectedMeasurement));
        calculateAverageDistance(yearLocations.getValue());
    }

    // Calculate the average distance moved per day for the entire year using yearLocations
    private void calculateAverageDistance(List<LocationEntity> locationEntities) {
        if (locationEntities != null && !locationEntities.isEmpty()) {
            // Group locations by day, month, and year
            Map<Integer, Map<Integer, Map<Integer, List<LocationEntity>>>> groupedLocations = new HashMap<>();

            // Sort locations by date
            for (LocationEntity location : locationEntities) {
                int year = location.getYear();
                int month = location.getMonth();
                int day = location.getDay();

                if (!groupedLocations.containsKey(year)) { // If the year is not in the map
                    // Add the year to the map
                    groupedLocations.put(year, new HashMap<>());
                }

                if (!groupedLocations.get(year).containsKey(month)) { // If the month is not in the map
                    // Add the month to the map
                    groupedLocations.get(year).put(month, new HashMap<>());
                }

                if (!groupedLocations.get(year).get(month).containsKey(day)) { // If the day is not in the map
                    // Add the day to the map
                    groupedLocations.get(year).get(month).put(day, new ArrayList<>());
                }

                // Add the location to the map
                groupedLocations.get(year).get(month).get(day).add(location);
            }

            // Calculate average distance per day
            float totalDistance = 0.0f;
            int totalDays = 0;

            // Iterate through the maps
            for (Map<Integer, Map<Integer, List<LocationEntity>>> yearMap : groupedLocations.values()) {
                for (Map<Integer, List<LocationEntity>> monthMap : yearMap.values()) {
                    for (List<LocationEntity> dayLocations : monthMap.values()) {
                        // Calculate the distance for the day
                        float dayDistance = DistanceCalculator.calculateDistance(dayLocations);

                        // Convert the distance to the selected measurement system
                        float convertedDistance = DistanceCalculator.convertDistance(dayDistance, measureOption, userHeight, selectedMeasurement);

                        // Add the distance to the total distance and increment the total days
                        totalDistance += convertedDistance;
                        totalDays++;
                    }
                }
            }

            if (totalDays > 0) { // Ensure there's data to calculate the average
                // Set average distance
                float average = totalDistance / totalDays; String formattedAverage =
                        String.format("%.1f", average);

                // Parse the formatted average back to a Float and post it
                averageDistance.postValue(Float.parseFloat(formattedAverage));
            } else {
                averageDistance.postValue(0.0f); // Default value if no data
            }
        } else {
            averageDistance.postValue(0.0f); // Default value if no data
        }
    }
}
