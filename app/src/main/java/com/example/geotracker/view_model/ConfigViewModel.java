package com.example.geotracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.geotracker.model.LocationRepository;

// View model for the configuration activity
public class ConfigViewModel extends AndroidViewModel {
    private static final String LOG_TAG = ConfigViewModel.class.getSimpleName(); // Log tag

    private LocationRepository repository; // Repository for the database

    // Constructor
    public ConfigViewModel(@NonNull Application application) {
        super(application);

        // Set up repository
        repository = new LocationRepository(application);
    }

    // Wipe all data
    public void wipeData() {
        repository.wipeData(); // Tell repository to wipe data from database
    }
}
