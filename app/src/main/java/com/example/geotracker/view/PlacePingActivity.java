package com.example.geotracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityMainBinding;
import com.example.geotracker.databinding.ActivityPlacePingBinding;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.view_model.MainViewModel;
import com.example.geotracker.view_model.PlacePingViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlacePingActivity extends AppCompatActivity {
    private GeoPoint markerPosition; // Position of the marker
    private MapView mapView; // Map instance]
    private PlacePingViewModel placePingViewModel; // View model
    private Marker userPlacedMarker; // To keep track of the user-placed marker


    // List to store markers created in this class
    private List<Marker> createdMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ping);

        // Set up data binding
        ActivityPlacePingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_place_ping);
        placePingViewModel = new ViewModelProvider(this).get(PlacePingViewModel.class);
        binding.setViewModel(placePingViewModel);
        binding.setLifecycleOwner(this);

        // set up save button
        findViewById(R.id.saveBtn).setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("latitude", placePingViewModel.getPingLatitude().getValue());
            data.putExtra("longitude", placePingViewModel.getPingLongitude().getValue());
            setResult(RESULT_OK, data);
            finish();
            Log.d("PlacePingActivity", "Save button clicked");
        });

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(15.0);

        // Create a MapEventsReceiver to handle map clicks
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                // Handle the map click here
                placeMarker(geoPoint);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                return false;
            }
        };

        // Create a MapEventsOverlay and add it to the map
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // Update map
        updateMap(mapView);

    }

    public void updateMap(MapView mapView) {
        final boolean[] centered = {false}; // Flag to indicate if the map has been centered

        LiveData<List<LocationEntity>> allLocations = placePingViewModel.getAllLocations();

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
                    placePingViewModel.setLatitude(latitude);
                    placePingViewModel.setLongitude(longitude);

                    GeoPoint startPoint = new GeoPoint(latitude, longitude);
                    if (mapView != null) {
                        // Set up marker
                        Marker locationMarker = new Marker(mapView);
                        locationMarker.setPosition(startPoint);
                        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        locationMarker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_user_icon));
                        locationMarker.setInfoWindow(null);

                        // Clear only the markers created in this class
                        clearCreatedMarkers(mapView);

                        // Add the new marker to the list and the map
                        createdMarkers.add(locationMarker);
                        mapView.getOverlays().addAll(createdMarkers);

                        if (!centered[0]) {
                            mapView.getController().setCenter(startPoint);
                            centered[0] = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Method to clear markers created in this class
    private void clearCreatedMarkers(MapView mapView) {
        // Remove all markers from the map
        for (Marker marker : createdMarkers) {
            mapView.getOverlays().remove(marker);
        }

        // Clear the list
        createdMarkers.clear();
    }

    private void placeMarker(GeoPoint geoPoint) {
        // Remove the previous user-placed marker if it exists
        if (userPlacedMarker != null) {
            mapView.getOverlays().remove(userPlacedMarker);
        }

        // Create a marker for the user-placed location and add it to the map
        userPlacedMarker = new Marker(mapView);
        userPlacedMarker.setPosition(geoPoint);
        userPlacedMarker.setInfoWindow(null);
        userPlacedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        userPlacedMarker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_ping_icon));
        mapView.getOverlays().add(userPlacedMarker);

        // Redraw the map
        mapView.invalidate();

        // Set the marker position in the view model
        placePingViewModel.setPingLocation(geoPoint.getLatitude(), geoPoint.getLongitude());

    }
}