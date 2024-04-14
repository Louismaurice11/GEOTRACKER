package com.example.geotracker.helper_classes;

import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.view_model.MainViewModel;

import java.util.Calendar;

// LocationListener is an interface that is used for receiving notifications from the LocationManager when the location has changed.
public class MyLocationListener implements LocationListener {
    public static final String LOG_TAG = MyLocationListener.class.getSimpleName(); // Log tag

    private final MainViewModel mainViewModel; // The ViewModel

    // Constructor
    public MyLocationListener(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    // Called when the location has changed.
    @Override
    public void onLocationChanged(Location location) {
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Get the current day, month, and year
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar months are zero-based
        int year = calendar.get(Calendar.YEAR);

        // Create a new LocationEntity with all the data
        LocationEntity newLocation = new LocationEntity(
                location.getLatitude(),
                location.getLongitude(),
                timestamp,
                day,
                month,
                year,
                false
        );

        // Add the location to the database using your ViewModel
        mainViewModel.insertLocation(newLocation);
    }

    // The following methods are not used in this program, but are required by the LocationListener interface
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d(LOG_TAG, "onStatusChanged: " + provider + " " + status);
    }
    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d(LOG_TAG, "onProviderEnabled: " + provider);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d(LOG_TAG, "onProviderDisabled: " + provider);
    }
}

