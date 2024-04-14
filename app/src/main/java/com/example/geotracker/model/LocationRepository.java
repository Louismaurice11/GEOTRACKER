package com.example.geotracker.model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.geotracker.helper_classes.DateConvertor;

import java.util.List;

// Repository the database
public class LocationRepository {
    public static final String LOG_TAG = LocationRepository.class.getSimpleName(); // Log tag

    private final LocationDao locationDao; // The DAO
    private final LiveData<List<LocationEntity>> allLocations; // The cached list of locations

    // Constructor
    public LocationRepository(Application application) {
        LocationDatabase database = LocationDatabase.getInstance(application);
        locationDao = database.locationDao();
        allLocations = locationDao.getAllLocations();
        Log.d(LOG_TAG, "LocationRepository created");
    }

    // Location queries
    public LiveData<List<LocationEntity>> getAllLocations() {
        return allLocations;
    }
    public LiveData<List<LocationEntity>> getLocationById(long id) {
        return locationDao.getLocationById(id);
    }

    // get most recent location
    public LiveData<LocationEntity> getMostRecentLocation() {
        return locationDao.getMostRecentLocation();
    }
    public LiveData<List<LocationEntity>> getLocationsFromToday(int day, int month, int year) {
        return locationDao.getLocationsFromToday(day, month, year);
    }
    public LiveData<List<LocationEntity>> getLocationsFromLastMonth(int month, int year) {
        return locationDao.getLocationsFromLastMonth(month, year);
    }
    public LiveData<List<LocationEntity>> getLocationsFromLastYear(int year) {
        return locationDao.getLocationsFromLastYear(year);
    }
    public void insertLocation(LocationEntity location) {
        locationDao.insertLocation(location);
    }
    public void deleteAllLocations() {
        locationDao.deleteAllLocations();
        locationDao.deleteAllAnnotations();
    }
    public void deleteLocationsFromToday(int day, int month, int year) {
        locationDao.deleteLocationsFromToday(day, month, year);
    }

    // Annotation queries
    public void insertAnnotation(AnnotationEntity annotation) {
        locationDao.deleteAnnotationsFromToday(annotation.getDay(), annotation.getMonth(), annotation.getYear());
        locationDao.insertAnnotation(annotation);
    }
    public void deleteAnnotationsFromToday(int day, int month, int year) {
        locationDao.deleteAnnotationsFromToday(day, month, year);
    }
    public LiveData<List<AnnotationEntity>> getAnnotationsFromToday(int day, int month, int year) {
        return locationDao.getAnnotationsFromToday(day, month, year);
    }

    // Reminder queries
    public void insertReminder(ReminderEntity reminder) {
        locationDao.insertReminder(reminder);
    }
    public void deleteReminder(double latitude, double longitude) {
        locationDao.deleteReminder(latitude, longitude);
    }
    public LiveData<List<ReminderEntity>> getAllReminders() {
        return locationDao.getAllReminders();
    }
    public void deleteAllReminders() {
        locationDao.deleteAllReminders();
    }

    public void deleteReminderById(long id) {
        locationDao.deleteReminderById(id);
    }

    // ReminderRecord queries
    public void insertReminderRecord(ReminderRecordEntity reminderRecord) {
        locationDao.insertReminderRecord(reminderRecord);
    }
    public LiveData<List<ReminderRecordEntity>> getAllReminderRecords() {
        return locationDao.getAllReminderRecords();
    }
    public LiveData<List<ReminderRecordEntity>> getReminderRecordsByReminderId(long reminderId) {
        return locationDao.getReminderRecordsByReminderId(reminderId);
    }
    public LiveData<List<ReminderRecordEntity>> getReminderRecordsFromToday(int day, int month, int year) {
        return locationDao.getReminderRecordsFromToday(day, month, year);
    }
    public void deleteAllReminderRecords() {
        locationDao.deleteAllReminderRecords();
    }
    public void deleteReminderRecordsByReminderId(long reminderId) {
        locationDao.deleteReminderRecordsByReminderId(reminderId);
    }
    public LiveData<List<ReminderRecordEntity>> getDuplicateReminderRecords(int day, int month, int year, long reminderId) {
        return locationDao.getDuplicateReminderRecords(day, month, year, reminderId);
    }
    public void deleteDuplicateReminderRecords(int day, int month, int year, long reminderId) {
        locationDao.deleteDuplicateReminderRecords(day, month, year, reminderId);
    }


    // Wipe all data
    public void wipeData() {
        locationDao.deleteAllReminderRecords();
        locationDao.deleteAllReminders();
        locationDao.deleteAllLocations();
        locationDao.deleteAllAnnotations();
    }
}
