package com.example.geotracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.geotracker.R;
import com.example.geotracker.helper_classes.MyLocationListener;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.view_model.MainViewModel;

import java.util.Calendar;

public class LocationTrackerService extends Service {
    private static final String LOG_TAG = LocationTrackerService.class.getSimpleName(); // Log tag

    private static final String NOTIFICATION_CHANNEL_ID = "LocationTrackerServiceChannel"; // Channel ID for the notification
    private static final int NOTIFICATION_ID = 1; // Notification ID for the notification
    private LocationManager locationManager; // LocationManager to request location updates
    private MyLocationListener locationListener; // LocationListener to handle location updates
    private final IBinder binder = new LocalBinder(); // Binder for clients to access the service
    private MainViewModel mainViewModel; // ViewModel to access the database
    private boolean isTracking = false; // Flag to indicate if the service is tracking

    // Get if the service is tracking
    public boolean isTracking() {
        return isTracking;
    }

    // Called when the service is created
    @Override
    public void onCreate() {
        super.onCreate();
        // Create a notification channel for the foreground service
        createNotificationChannel();
        // Initialize LocationManager and LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize ViewModel
        mainViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(MainViewModel.class);

        locationListener = new MyLocationListener(mainViewModel);
    }

    // Called when the service is destroyed
    @Override
    public void onDestroy() {
        stopLocationTracking();
        super.onDestroy();
    }

    // Called when the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationTracking();
        return START_STICKY;
    }

    // Called when a client binds to the service
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Binder class for clients to access the service
    public class LocalBinder extends Binder {
        public LocationTrackerService getService() {
            return LocationTrackerService.this;
        }
    }

    // Stop location tracking
    public void stopLocationTracking() {
        // Stop location updates
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        // Remove the service from the foreground
        stopForeground(true);


        // Get the current day, month, and year
        Calendar calendar = Calendar.getInstance();

        try{
            // Create a new LocationEntity with all the data to mark the end of tracking
            LocationRepository locationRepository = new LocationRepository(getApplication());
            locationRepository.insertLocation(new LocationEntity(
                    mainViewModel.getLatitude().getValue(),
                    mainViewModel.getLongitude().getValue(),
                    System.currentTimeMillis(),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR),
                    true));
            isTracking = false;

        } catch (Exception e) {
            Log.e(LOG_TAG, "No location data");
        }
    }

    // Start location tracking
    public void startLocationTracking() {
        // Request location updates every 5 seconds or when the user moves at least 5 meters
        try {
            // Create a notification for the foreground service
            startForeground(NOTIFICATION_ID, createNotification());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, 5, locationListener);
            isTracking = true;
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Location permission not granted");
        }
    }

    // Create the notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Location Tracker Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // Create a notification for the foreground service
    private Notification createNotification() {
        Notification notification = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Geotracker")
                .setContentText("Currently Tracking Location")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setSound(null)
                .build();

        return notification;
    }
}