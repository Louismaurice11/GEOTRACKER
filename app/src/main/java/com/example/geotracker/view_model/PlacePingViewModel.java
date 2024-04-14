package com.example.geotracker.view_model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.geotracker.OSM.PingInfoWindow;
import com.example.geotracker.R;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderEntity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PlacePingViewModel extends AndroidViewModel{
    private static final String LOG_TAG = PlacePingViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Live data
    private final LiveData<List<LocationEntity>> allLocations; // Live data for all locations
    private final MutableLiveData<Double> latitude = new MutableLiveData<>(0.0); // Latitude of current location
    private final MutableLiveData<Double> longitude = new MutableLiveData<>(0.0); // Longitude of current location
    private final MutableLiveData<Double> pingLatitude = new MutableLiveData<>(0.0); // Latitude of ping location
    private final MutableLiveData<Double> pingLongitude = new MutableLiveData<>(0.0); // Longitude of ping location
    private final Context context; // Context




    // Constructor
    public PlacePingViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();

        // Get all locations from the database
        repository = new LocationRepository(application);
        allLocations = repository.getAllLocations();
    }




    // set the latitude and longitude of the ping location
    public void setPingLocation(double latitude, double longitude) {
        // Set the latitude and longitude
        pingLatitude.setValue(latitude);
        pingLongitude.setValue(longitude);

        Log.d(LOG_TAG, "Ping location set to: " + latitude + ", " + longitude);
    }

    // get the latitude of the ping location
    public LiveData<Double> getPingLatitude() {
        return pingLatitude;
    }

    // get the longitude of the ping location
    public LiveData<Double> getPingLongitude() {
        return pingLongitude;
    }

    public LiveData<List<LocationEntity>> getAllLocations() {
        return allLocations;
    }

    public void setLatitude(double latitude) {
        this.latitude.postValue(latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude.postValue(longitude);
    }
}
