package com.example.geotracker.view_model;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.geotracker.OSM.PingInfoWindow;
import com.example.geotracker.R;
import com.example.geotracker.helper_classes.DistanceCalculator;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderEntity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

// ViewModel for the main activity
public class MainViewModel extends AndroidViewModel {
    public static final String LOG_TAG = MainViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Live data
    private final LiveData<List<LocationEntity>> allLocations; // Live data for all locations
    private final LiveData<List<ReminderEntity>> allReminders; // Live data for all reminders
    private final MutableLiveData<Double> latitude = new MutableLiveData<>(0.0); // Latitude of current location
    private final MutableLiveData<Double> longitude = new MutableLiveData<>(0.0); // Longitude of current location
    private final MutableLiveData<String> speed = new MutableLiveData<>("N/A"); // Most recent speed

    private List<LocationEntity> localLocations; // List of locations
    private List<ReminderEntity> localReminders; // List of reminders

    private MapView map; // Map instance
    private final Context context; // Context

    // Constructor
    public MainViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();

        // Get all locations from the database
        repository = new LocationRepository(application);
        allLocations = repository.getAllLocations();
        allReminders = repository.getAllReminders();
    }

    // Getters
    public LiveData<List<LocationEntity>> getAllLocations() {
        return allLocations;
    }

    public MutableLiveData<Double> getLatitude() {
        return latitude;
    }

    public MutableLiveData<Double> getLongitude() {
        return longitude;
    }

    public MutableLiveData<String> getSpeed(){
        return speed;
    }


    // Insert a location into the database
    public void insertLocation(LocationEntity location) {
        repository.insertLocation(location);
        latitude.setValue(location.getLatitude());
        longitude.setValue(location.getLongitude());
    }

    // Delete all locations from the database
    public void deleteAllLocations() {
        repository.deleteAllLocations();
    }

    public void setLatitude(double latitude) {
        this.latitude.postValue(latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude.postValue(longitude);
    }

    public LiveData<List<ReminderEntity>> getAllReminders() {
        return allReminders;
    }

    public void setSpeed(String speed) {
        this.speed.postValue(speed);
    }
}