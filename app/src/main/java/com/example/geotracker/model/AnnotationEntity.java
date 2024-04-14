package com.example.geotracker.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Entity class for annotations
@Entity(tableName = "annotations")
public class AnnotationEntity {
    public static final String LOG_TAG = AnnotationEntity.class.getSimpleName(); // Log tag

    // Fields
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private final int day;
    private final int month;
    private final int year;
    private String annotation;
    private final float rating;

    // Constructor
    public AnnotationEntity(int day, int month, int year, String annotation, float rating) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.annotation = annotation;
        this.rating = rating;
        Log.d(LOG_TAG, "Annotation created: " + annotation + " " + day + " " + month + " " + year);
    }

    // Getters and setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
    public String getAnnotation() {
        return annotation;
    }
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    public float getRating() {
        return rating;
    }
}
