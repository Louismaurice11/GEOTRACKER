package com.example.geotracker.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.geotracker.OSM.PingInfoWindow;
import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityMainBinding;
import com.example.geotracker.helper_classes.DistanceCalculator;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.model.ReminderEntity;
import com.example.geotracker.services.LocationTrackerService;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.view_model.MainViewModel;
import com.example.geotracker.services.PingService;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Main activity for the app
public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName(); // Log tag

    // String constants
    public static final String ON_MESSAGE = "Tracking service is started";
    public static final String OFF_MESSAGE = "Tracking service is stopped";

    private boolean pingServiceBound = false; // Flag to indicate pingService if the service is bound
    private boolean locationTrackerServiceBound = false; // Flag to indicate locationTrackerService if the service is bound
    private MainViewModel mainViewModel; // The ViewModel
    private LocationTrackerService locationTrackerService; // The locationTrackerService

    private MapView map;

    private Marker currentUserMarker;
    private List<Marker> reminderMarkers = new ArrayList<>(); // List to store reminder markers


    // Called when the activity is first created.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Set up data binding
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);

        // Set up navigation bar
        NavBarConstructor.addOnClick(findViewById(R.id.trackerBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.pingBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.historyBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.settingsBtn), this);

        // Set up map
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(17.0);

        // request permissions for map
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET}, 1);

        // Set up map controls
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);
        updateMap();

        TextView activateTextView = findViewById(R.id.activateTextView);
        activateTextView.setText(OFF_MESSAGE);

        // Set up start button to start tracking the service and the ping service
        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!locationTrackerServiceBound) {
                    startTrackingService();
                }else {
                    if (!locationTrackerService.isTracking()) {
                        locationTrackerService.startLocationTracking();
                    }
                }
                if(!pingServiceBound) {
                    startPingService();
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            // Tell the user that the service is started
            activateTextView.setText(ON_MESSAGE);
        });

        // Set up stop button to stop tracking the service
        Button stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!locationTrackerServiceBound) {
                    startTrackingService();
                }else {
                    if(locationTrackerService.isTracking()) {
                        locationTrackerService.stopLocationTracking();
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            // Tell the user that the service is stopped
            activateTextView.setText(OFF_MESSAGE);
        });
    }

    private void updateMap(){
        LiveData<List<LocationEntity>> allLocations = mainViewModel.getAllLocations();

        // Observe all locations
        allLocations.observe(this, new Observer<List<LocationEntity>>() {
            @Override
            public void onChanged(List<LocationEntity> locationEntities) {
                if (locationEntities != null && !locationEntities.isEmpty()) { // If there are locations
                    // Get the latest location
                    LocationEntity latestLocation = locationEntities.get(locationEntities.size() - 1);

                    double latitude = latestLocation.getLatitude();
                    double longitude = latestLocation.getLongitude();
                    // Set the latitude and longitude
                    mainViewModel.setLatitude(latitude);
                    mainViewModel.setLongitude(longitude);

                    GeoPoint startPoint = new GeoPoint(latitude, longitude);

                    // place user marker
                    addUserMarker(startPoint);

                    if (locationEntities.size() > 1) { // If there is more than one location
                        // Get the second latest location
                        LocationEntity secondLatestLocation = locationEntities.get(locationEntities.size() - 2);

                        // Create two location objects for calculating distance
                        Location location1 = new Location("location1");
                        location1.setLatitude(latestLocation.getLatitude());
                        location1.setLongitude(latestLocation.getLongitude());
                        Location location2 = new Location("location2");
                        location2.setLatitude(secondLatestLocation.getLatitude());
                        location2.setLongitude(secondLatestLocation.getLongitude());

                        // Calculate the speed
                        float distance = location1.distanceTo(location2);
                        float timeInSeconds = (latestLocation.getTimestamp() - secondLatestLocation.getTimestamp()) / 1000.0f; // Convert to seconds
                        float speedInMetersPerSecond = distance / timeInSeconds;
                        float speedInKilometersPerHour = speedInMetersPerSecond * 3.6f;

                        // Round to 1 decimal place
                        speedInKilometersPerHour = Math.round(speedInKilometersPerHour * 10.0) / 10.0f;

                        mainViewModel.setSpeed(String.valueOf(speedInKilometersPerHour) + " km/h");
                    }
                }

            }
        });

        // get all reminders from the viewModel
        LiveData<List<ReminderEntity>> allReminders = mainViewModel.getAllReminders();

        // Observe all reminders
        allReminders.observe(MainActivity.this, new Observer<List<ReminderEntity>>() {
            @Override
            public void onChanged(List<ReminderEntity> reminderEntities) {
                clearReminderMarkers(); // Clear all reminder markers
                if (reminderEntities != null && !reminderEntities.isEmpty()) { // If there are reminders
                    // For each reminder
                    for (ReminderEntity reminder : reminderEntities) {
                        // Get the latitude and longitude of the reminder
                        double reminderLatitude = reminder.getLatitude();
                        double reminderLongitude = reminder.getLongitude();

                        addReminderMarker(reminderLatitude, reminderLongitude, reminder.getTitle());
                    }
                }
            }
        });
    }

    // Clear reminder markers
    private void clearReminderMarkers() {
        for (Marker marker : reminderMarkers) {
            map.getOverlays().remove(marker);
        }
        reminderMarkers.clear();
    }

    // Method to add a user marker
    private void addUserMarker(GeoPoint startPoint) {
        // Remove the previous user marker if it exists
        if (currentUserMarker != null) {
            map.getOverlays().remove(currentUserMarker);
        }

        // Set up user marker
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_user_icon));
        startMarker.setInfoWindow(null);

        // Set the current user marker
        currentUserMarker = startMarker;

        // Add the user marker to the map
        map.getOverlays().add(startMarker);
        map.getController().setCenter(startPoint);
    }

    // Method to add a reminder marker
    private void addReminderMarker(double latitude, double longitude, String title) {
        // Set up reminder marker
        Marker reminderMarker = new Marker(map);
        reminderMarker.setPosition(new GeoPoint(latitude, longitude));
        reminderMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        reminderMarker.setTitle(title);
        reminderMarker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_ping_icon));

        // Create and set the custom info window
        PingInfoWindow infoWindow = new PingInfoWindow(R.layout.ping_window_layout, map);
        reminderMarker.setInfoWindow(infoWindow);

        // Add the reminder marker to the map and the reminderMarkers list
        reminderMarkers.add(reminderMarker);
        map.getOverlays().add(reminderMarker);
    }

    // Start the LocationTrackerService
    private void startTrackingService() {
        // Bind to the service
        Intent intent = new Intent(this, LocationTrackerService.class);
        bindService(intent, locationTrackerServiceConnection, Context.BIND_AUTO_CREATE);
        startForegroundService(intent);
    }

    // Start the PingService
    private void startPingService() {
        Log.d(LOG_TAG, "startPingService: ");
        // Bind to the service
        Intent intent = new Intent(this, PingService.class);
        bindService(intent, pingServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    // Called when the user responds to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startTrackingService();
            } else {
                // Show a message to the user and provide an option to go to app settings.
                Snackbar.make(findViewById(android.R.id.content),
                                "Location permission is required for this feature. Please enable it in App Settings.",
                                Snackbar.LENGTH_LONG)
                        .setAction("SETTINGS", view -> {
                            // Open the app settings to allow the user to enable permissions.
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .show();
            }

            TextView textView = findViewById(R.id.activateTextView);
            textView.setText(OFF_MESSAGE);
        }
    }

    // Called when the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (locationTrackerServiceBound) {
            unbindService(locationTrackerServiceConnection);
            stopService(new Intent(this, LocationTrackerService.class));
        }
        if (pingServiceBound) {
            unbindService(pingServiceConnection);
            stopService(new Intent(this, PingService.class));
        }
    }

    // Service connection to handle connection and disconnection to the locationTrackerService
    private ServiceConnection locationTrackerServiceConnection = new ServiceConnection() {
        // Triggered if the service is connected
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Linking the service to our locationTrackerService variable.
            Log.d(LOG_TAG, "onServiceConnected: ");
            LocationTrackerService.LocalBinder binder = (LocationTrackerService.LocalBinder) service;
            locationTrackerService = binder.getService();

            // Flag that the binding is successful.
            locationTrackerServiceBound = true;
        }

        // Triggered if the service unexpectedly disconnects
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Flagging that the binding is no longer active.
            locationTrackerServiceBound = false;
        }
    };

    // Service connection to handle connection and disconnection to the pingService
    private ServiceConnection pingServiceConnection = new ServiceConnection() {
        // Triggered if the service is connected
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Flag that the binding is successful.
            pingServiceBound = true;
        }

        // Triggered if the service unexpectedly disconnects
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Flagging that the binding is no longer active.
            pingServiceBound = false;
        }
    };
}