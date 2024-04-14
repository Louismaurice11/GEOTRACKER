package com.example.geotracker.model;

import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Entity class for locations
@Entity(tableName = "locations")
public class LocationEntity {
    public static final String LOG_TAG = LocationEntity.class.getSimpleName(); // Log tag

    //Content provider constants
    public static final String AUTHORITY = "com.example.geotracker";
    public static final String TABLE_NAME = "locations";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YEAR = "year";

    // Fields
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private final double latitude;
    private final double longitude;
    private final long timestamp;
    private int day;
    private int month;
    private int year;
    private boolean LastLocation; // Flag to indicate if this is the last location before location tracking stopped

    // Constructor
    public LocationEntity(double latitude, double longitude, long timestamp, int day, int month, int year, boolean LastLocation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.day = day;
        this.month = month;
        this.year = year;
        this.LastLocation = LastLocation;
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
    public long getTimestamp() {
        return timestamp;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public boolean getLastLocation() {
        return LastLocation;
    }
}