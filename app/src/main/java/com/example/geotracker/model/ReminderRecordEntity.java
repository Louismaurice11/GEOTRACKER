package com.example.geotracker.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminder_records")
public class ReminderRecordEntity {
    public static final String LOG_TAG = ReminderRecordEntity.class.getSimpleName(); // Log tag

    // Fields
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private final long reminderId;
    private final int day;
    private final int month;
    private final int year;


    // Constructor
    public ReminderRecordEntity(long reminderId, int day, int month, int year) {
        this.reminderId = reminderId;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Getters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getReminderId() {
        return reminderId;
    }
    public int getDay() {
        return day;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
}
