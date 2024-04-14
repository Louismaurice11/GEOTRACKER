package com.example.geotracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderEntity;

import java.util.List;

// View model for the reminder activity
public class ReminderViewModel extends AndroidViewModel {
    private static final String LOG_TAG = ReminderViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Live data
    private final LiveData<List<ReminderEntity>> reminders; // Live data for all reminders

    // Constructor
    public ReminderViewModel(@NonNull Application application) {
        super(application);

        // Set up repository
        repository = new LocationRepository(application);

        // Get all reminders
        reminders = repository.getAllReminders();
    }

    // Get all reminders
    public LiveData<List<ReminderEntity>> getAllReminders() {
        return reminders;
    }

    // Delete a reminder by id
    public void deleteReminderById(long id) {
        repository.deleteReminderById(id);
    }

}
