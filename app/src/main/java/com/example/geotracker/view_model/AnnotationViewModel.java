package com.example.geotracker.view_model;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.geotracker.helper_classes.DistanceCalculator;
import com.example.geotracker.model.AnnotationEntity;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderRecordEntity;

import java.util.List;

// ViewModel for annotations
public class AnnotationViewModel extends AndroidViewModel{
    public static final String LOG_TAG = AnnotationViewModel.class.getSimpleName(); // Log tag

    private final LocationRepository repository; // Repository for the database

    // Date of the annotation
    private int year;
    private int month;
    private int day;

    // Live data
    private final MutableLiveData<String> date = new MutableLiveData<>("NULL"); // Date of the annotation
    private final MutableLiveData<String> annotation = new MutableLiveData<>(""); // Annotation for this date
    private final MutableLiveData<Float> distance = new MutableLiveData<>(0.0f); // Distance traveled for this date
    private final MutableLiveData<Float> rating = new MutableLiveData<>(0.0f); // Rating for this date
    private final MutableLiveData<Integer> numReminders = new MutableLiveData<>(0); // Number of reminders received for this date
    private final MutableLiveData<String> distanceUnit = new MutableLiveData<>(" steps"); // Unit of distance
    private LiveData<List<AnnotationEntity>> annotations; // Live data for annotations for this date
    private LiveData<List<LocationEntity>> locations; // Live data for locations for this date
    private LiveData<List<ReminderRecordEntity>> reminderRecords; // Live data for reminder records for this date

    // Constructor
    public AnnotationViewModel(@NonNull Application application) {
        super(application);
        repository = new LocationRepository(application);
    }

    // Get the date for this annotation
    public MutableLiveData<String> getDate() {
        return date;
    }

    // Get the annotation for this date
    public MutableLiveData<String> getAnnotation() {
        return annotation;
    }

    // Get the number of annotations for this date
    public int getNumAnnotations() {
        if(repository.getAnnotationsFromToday(day, month, year).getValue() != null)
            return repository.getAnnotationsFromToday(day, month, year).getValue().size();
        else
            return 0;
    }

    // Get the number of reminders received for this date
    public MutableLiveData<Integer> getNumReminders() {
        return numReminders;
    }

    // Delete annotations for this date
    public void deleteAnnotationsFromToday(int day, int month, int year) {
        repository.deleteAnnotationsFromToday(day, month, year);
    }

    // Insert annotation into database
    public void insertAnnotation(AnnotationEntity annotation) {
        repository.insertAnnotation(annotation);
    }

    // Set annotation
    public void setAnnotation(String annotation) {
        this.annotation.postValue(annotation);
    }

    // Get distance traveled for this date
    public MutableLiveData<Float> getDistance() {
        return distance;
    }

    // Rating getter and setter
    public MutableLiveData<Float> getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating.postValue(rating);
    }

    // Distance unit getter and setter
    public MutableLiveData<String> getDistanceUnit() {
        return distanceUnit;
    }
    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit.postValue(distanceUnit);
    }

    // Set the date for this annotation and get annotation from database
    public void setDate(int year, int month, int day, int measureOption, float height, String selectedMeasurement) {
        // Set date
        this.year = year;
        this.month = month;
        this.day = day;
        date.postValue(day + "/" + month + "/" +  year);

        // Get annotation from database
        annotations = repository.getAnnotationsFromToday(day, month, year);
        annotations.observeForever(annotations -> {
            if (annotations != null && !annotations.isEmpty()) { // If annotations for the specified day are available
                // Update the annotation and rating to the user's input
                annotation.postValue(annotations.get(annotations.size()-1).getAnnotation());
                rating.postValue(annotations.get(annotations.size()-1).getRating());
            } else { // If no annotations for the specified day are available
                // Reset the annotation
                annotation.postValue("");
            }
        });

        // Get specified day's locations from database
        locations = repository.getLocationsFromToday(day, month, year);

        // Observe the locations to calculate the distance traveled
        locations.observeForever(locationEntities -> {
            if (locationEntities != null && !locationEntities.isEmpty()) { // If locations for the specified day are available
                // Calculate the distance traveled
                this.distance.postValue(DistanceCalculator.
                        convertDistance(DistanceCalculator.
                                calculateDistance(locationEntities), measureOption, height, selectedMeasurement));
            }
        });

        // Get reminder records from database
        reminderRecords = repository.getReminderRecordsFromToday(day, month, year);

        // Observe the reminder records to get the number of reminders received
        reminderRecords.observeForever(reminderRecordEntities -> {
            if (reminderRecordEntities != null && !reminderRecordEntities.isEmpty()) { // If reminder records for the specified day are available
                // Reminder records for the specified day are available
                numReminders.postValue(reminderRecordEntities.size());
            }
        });
    }

    // Remove observers
    public void removeObservers(LifecycleOwner owner) {
        locations.removeObservers(owner);
        annotations.removeObservers(owner);
        reminderRecords.removeObservers(owner);
    }
}
