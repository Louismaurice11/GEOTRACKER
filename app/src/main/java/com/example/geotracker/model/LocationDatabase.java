package com.example.geotracker.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Database class for the application
@Database(entities = {LocationEntity.class, AnnotationEntity.class, ReminderEntity.class,
        ReminderRecordEntity.class}, version = 8, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    public static final String LOG_TAG = LocationDatabase.class.getSimpleName(); // Log tag

    private static LocationDatabase instance; // Singleton instance
    public abstract LocationDao locationDao(); // Location DAO

    // Singleton instance getter
    @SuppressLint("SuspiciousIndentation")
    public static LocationDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (LocationDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LocationDatabase.class,
                                    "location_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                            Log.d(LOG_TAG, "database created");
                }
            }
        }
        return instance;
    }
}