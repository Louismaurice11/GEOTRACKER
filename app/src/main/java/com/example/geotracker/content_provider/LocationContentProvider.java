package com.example.geotracker.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.geotracker.model.LocationDao;
import com.example.geotracker.model.LocationDatabase;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;

// Content provider for the location table
public class LocationContentProvider extends ContentProvider {
    private String LOG_TAG = LocationContentProvider.class.getSimpleName(); // Log tag

    public static final Uri URI_LOCATION = Uri.parse("content://" + LocationEntity.AUTHORITY
            + "/" + LocationEntity.TABLE_NAME); // URI for the location table
    private static final int CODE_LOCATION_DIR = 1; // Code for the location directory
    private static final int CODE_LOCATION_ITEM = 2; // Code for the location item
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH); // Uri matcher

    @Override
    public boolean onCreate() {
        return true;
    }

    // Query the database
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Match the URI
        final int code = MATCHER.match(uri);
        if (code == CODE_LOCATION_DIR || code == CODE_LOCATION_ITEM) { // If the URI matches
            // Get the context
            final Context context = getContext();

            if (context == null) { // If the context is null
                // Return null
                return null;
            }

            // Get the location dao
            LocationDao locationDao = LocationDatabase.getInstance(context).locationDao();

            // Get the cursor
            final Cursor cursor;
            if (code == CODE_LOCATION_DIR) { // If the URI matches the location directory
                // Get all locations
                cursor = (Cursor) locationDao.getAllLocations();
            } else  if (code == CODE_LOCATION_ITEM){ // If the URI matches a location item
                // Get location by id
                cursor = (Cursor) locationDao.getLocationById(ContentUris.parseId(uri));
            } else { // If the URI does not match
                // Throw an exception
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            // Set notification URI
            cursor.setNotificationUri(context.getContentResolver(), uri);

            // Return the cursor
            return cursor;
        }
        return null;
    }

    // Get the type of the URI
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Match the URI
        switch (MATCHER.match(uri)) {
            case CODE_LOCATION_DIR: // If the URI matches the location directory
                // Return the type
                return "vnd.android.cursor.dir/" + LocationEntity.AUTHORITY + "." + LocationEntity.TABLE_NAME;
            case CODE_LOCATION_ITEM: // If the URI matches a location item
                // Return the type
                return "vnd.android.cursor.item/" + LocationEntity.AUTHORITY + "." + LocationEntity.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    // Insert a location into the database - not used
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    // Delete a location from the database - not used
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    // Update a location in the database - not used
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
