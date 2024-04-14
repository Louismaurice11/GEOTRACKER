package com.example.geotracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderEntity;

import java.util.List;

// ViewModel for the new reminder activity
public class NewReminderViewModel extends AndroidViewModel {
    private static final String LOG_TAG = NewReminderViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Live data
    private final LiveData<List<ReminderEntity>> reminders;
    private final MutableLiveData<Double> latitude = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> longitude = new MutableLiveData<>(0.0);

    // Constructor
    public NewReminderViewModel(@NonNull Application application) {
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

    // insert a reminder
    public void insertReminder(ReminderEntity reminder) {
        repository.insertReminder(reminder);
    }

    // delete all reminders
    public void deleteAllReminders() {
        repository.deleteAllReminders();
    }

    // set latitude and longitude
    public void setLocation(double latitude, double longitude) {
        this.latitude.postValue(latitude);
        this.longitude.postValue(longitude);
    }

    // get latitude
    public LiveData<Double> getLatitude() {
        return latitude;
    }

    // get longitude
    public LiveData<Double> getLongitude() {
        return longitude;
    }

    // set latitude
    public void setLatitude(double latitude) {
        this.latitude.postValue(latitude);
    }

    // set longitude
    public void setLongitude(double longitude) {
        this.longitude.postValue(longitude);
    }

}
