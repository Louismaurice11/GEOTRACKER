package com.example.geotracker.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Data access object for the database
@Dao
public interface LocationDao {
    // Location queries
    @Insert
    void insertLocation(LocationEntity location);
    @Query("SELECT * FROM locations")
    LiveData<List<LocationEntity>> getAllLocations();
    @Query("SELECT * FROM locations WHERE id = :id")
    LiveData<List<LocationEntity>> getLocationById(long id);
    @Query("DELETE FROM locations")
    void deleteAllLocations();
    @Query("DELETE FROM locations WHERE day = :day AND month = :month AND year = :year")
    void deleteLocationsFromToday(int day, int month, int year);
    @Query("SELECT * FROM locations WHERE day = :day AND month = :month AND year = :year")
    LiveData<List<LocationEntity>> getLocationsFromToday(int day, int month, int year);
    @Query("SELECT * FROM locations WHERE month = :month AND year = :year")
    LiveData<List<LocationEntity>> getLocationsFromLastMonth(int month, int year);
    @Query("SELECT * FROM locations WHERE year = :year")
    LiveData<List<LocationEntity>> getLocationsFromLastYear(int year);
    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    LiveData<LocationEntity> getMostRecentLocation();

    // Annotation queries
    @Insert
    void insertAnnotation(AnnotationEntity annotation);
    @Query("SELECT * FROM annotations")
    LiveData<List<AnnotationEntity>> getAllAnnotations();
    @Query("SELECT * FROM annotations WHERE day = :day AND month = :month AND year = :year")
    LiveData<List<AnnotationEntity>> getAnnotationsFromToday(int day, int month, int year);
    @Query("DELETE FROM annotations WHERE day = :day AND month = :month AND year = :year")
    void deleteAnnotationsFromToday(int day, int month, int year);
    @Query("DELETE FROM annotations")
    void deleteAllAnnotations();

    // Reminder queries
    @Insert
    void insertReminder(ReminderEntity reminder);
    @Query("SELECT * FROM reminders")
    LiveData<List<ReminderEntity>> getAllReminders();
    @Query("DELETE FROM reminders WHERE latitude = :latitude AND longitude = :longitude")
    void deleteReminder(double latitude, double longitude);
    @Query("DELETE FROM reminders")
    void deleteAllReminders();
    @Query("DELETE FROM reminders WHERE id = :id")
    void deleteReminderById(long id);

    // ReminderRecord queries
    @Insert
    void insertReminderRecord(ReminderRecordEntity reminderRecord);
    @Query("SELECT * FROM reminder_records")
    LiveData<List<ReminderRecordEntity>> getAllReminderRecords();
    @Query("SELECT * FROM reminder_records WHERE reminderId = :reminderId")
    LiveData<List<ReminderRecordEntity>> getReminderRecordsByReminderId(long reminderId);
    @Query("DELETE FROM reminder_records")
    void deleteAllReminderRecords();
    @Query("DELETE FROM reminder_records WHERE reminderId = :reminderId")
    void deleteReminderRecordsByReminderId(long reminderId);
    @Query("SELECT * FROM reminder_records WHERE day = :day AND month = :month AND year = :year")
    LiveData<List<ReminderRecordEntity>> getReminderRecordsFromToday(int day, int month, int year);
    @Query("SELECT * FROM reminder_records WHERE day = :day AND month = :month AND year = :year AND reminderId = :reminderId")
    LiveData<List<ReminderRecordEntity>> getDuplicateReminderRecords(int day, int month, int year, long reminderId);
    @Query("DELETE FROM reminder_records WHERE day = :day AND month = :month AND year = :year AND reminderId = :reminderId")
    void deleteDuplicateReminderRecords(int day, int month, int year, long reminderId);

}
