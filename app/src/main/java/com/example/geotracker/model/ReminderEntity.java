package com.example.geotracker.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "reminders")
public class ReminderEntity {
    public static final String LOG_TAG = ReminderEntity.class.getSimpleName(); // Log tag

    // Fields
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private final double latitude;
    private final double longitude;
    private final String title;

    // Constructor
    public ReminderEntity(double latitude, double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    // Getters and setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getTitle() {
        return title;
    }

}
