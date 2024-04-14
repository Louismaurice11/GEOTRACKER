package com.example.geotracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;

import java.util.List;

// View model for viewing history of locations
public class HistoryViewModel extends AndroidViewModel {
    private static final String LOG_TAG = HistoryViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Live data
    private final LiveData<List<LocationEntity>> allLocations; // Live data for all locations
    private MutableLiveData<String> date = new MutableLiveData<>(""); // Date of selected locations

    // date variables
    private int day = 0;
    private int month = 0;
    private int year = 0;

    // Constructor
    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new LocationRepository(application);
        allLocations = repository.getAllLocations();
    }

    // Get all locations
    public LiveData<List<LocationEntity>> getAllLocations() {
        return allLocations;
    }

    // Delete all locations
    public void deleteAllLocations() {
        repository.deleteAllLocations();
    }

    public void deleteLocationsFromDay(int day, int month, int year) {
        repository.deleteLocationsFromToday(day, month, year);
    }

    // Get date
    public MutableLiveData<String> getDate() {
        return date;
    }

    // Set date
    public void setDate(String date) {
        this.date.postValue(date);
    }

    // Get locations from selected date
    public LiveData<List<LocationEntity>> getLocationsFromDay(int day, int month, int year) {
        return repository.getLocationsFromToday(day, month, year);
    }

    // Getters and setters for date variables
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // Reset date variables
    public void resetDate() {
        this.day = 0;
        this.month = 0;
        this.year = 0;
    }

    // Check if date is clear
    public boolean isDateClear() {
        return day == 0 && month == 0 && year == 0;
    }
}
