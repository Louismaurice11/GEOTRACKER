package com.example.geotracker.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityHistoryBinding;
import com.example.geotracker.helper_classes.DateConvertor;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.view_model.HistoryViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

// Activity for viewing history of locations
public class HistoryActivity extends AppCompatActivity {
    private static final String LOG_TAG = HistoryActivity.class.getSimpleName(); // Log tag

    private HistoryViewModel historyViewModel; // View model for this activity

    private LiveData<List<LocationEntity>> locationLiveData; // LiveData for location data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up data binding
        ActivityHistoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        binding.setViewModel(historyViewModel);
        binding.setLifecycleOwner(this);

        // Set up navigation bar
        NavBarConstructor.addOnClick(findViewById(R.id.trackerBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.pingBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.homeBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.settingsBtn), this);

        // Set up list view
        ListView locationListView = findViewById(R.id.locationListView);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        locationListView.setAdapter(adapter);

        // Get all locations from the database and observe them
        locationLiveData = historyViewModel.getAllLocations();
        locationLiveData.observe(this, new Observer<List<LocationEntity>>() {
            @Override
            public void onChanged(@Nullable List<LocationEntity> locations) {
                adapter.clear();
                for (LocationEntity location : locations) {
                    String locationInfo = "Latitude: " + location.getLatitude() + "\nLongitude: " +
                            location.getLongitude() + "\nDate: " + location.getDay() +
                            "/" + location.getMonth() + "/" + location.getYear() +
                            "\nTimestamp: " + location.getTimestamp();

                    adapter.add(locationInfo);
                }
            }
        });


        findViewById(R.id.wipeBtn).setOnClickListener(v -> { // Delete all locations
            if (historyViewModel.isDateClear()) { // If date is clear
                showConfirmationDialog(() -> {
                    // Delete all locations from the database
                    historyViewModel.deleteAllLocations();
                });
            } else { // if a date is selected
                showConfirmationDialog(() -> {
                    // Delete all locations from the selected date
                    historyViewModel.deleteLocationsFromDay(historyViewModel.getDay(),
                            historyViewModel.getMonth(), historyViewModel.getYear());
                });
            }
        });

        findViewById(R.id.clearBtn).setOnClickListener(v -> { // Clear search
            // Remove observers from the previous LiveData
            if (locationLiveData != null) {
                locationLiveData.removeObservers(this);
            }
            // Get all locations from the database and observe them
            locationLiveData = historyViewModel.getAllLocations();
            locationLiveData.observe(this, new Observer<List<LocationEntity>>() {
                @Override
                public void onChanged(@Nullable List<LocationEntity> locations) {
                    adapter.clear();
                    for (LocationEntity location : locations) {
                        String locationInfo = "Latitude: " + location.getLatitude() + "\nLongitude: " +
                                location.getLongitude() + "\nDate: " + location.getDay() +
                                "/" + location.getMonth() + "/" + location.getYear() +
                                "\nTimestamp: " + location.getTimestamp();

                        adapter.add(locationInfo);
                    }
                }
            });


            // Reset the date
            historyViewModel.resetDate();
        });

        // Set up date search
        findViewById(R.id.searchBtn).setOnClickListener(v -> {
            historyViewModel.setDate(historyViewModel.getDate().getValue());
            int[] date = DateConvertor.parseDate(historyViewModel.getDate().getValue());

            // Check if date is valid
            if (date == null) { // If date is invalid
                // Display error message
                Snackbar.make(v, "Invalid date", Snackbar.LENGTH_LONG).show();
                historyViewModel.setDate("");
            } else { // If date is valid
                // Set the date in the view model
                historyViewModel.setDate(DateConvertor.convertToDateString(date[0], date[1], date[2]));

                // Remove observers from the previous LiveData
                if (locationLiveData != null) {
                    locationLiveData.removeObservers(this);
                }

                // Create a new LiveData to observe the updated date
                locationLiveData = historyViewModel.getLocationsFromDay(date[0], date[1], date[2]);

                // Observe the new LiveData for location data changes
                locationLiveData.observe(this, new Observer<List<LocationEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<LocationEntity> locations) {
                        adapter.clear();
                        for (LocationEntity location : locations) {
                            String locationInfo = "Latitude: " + location.getLatitude() + "\nLongitude: " +
                                    location.getLongitude() + "\nDate: " + location.getDay() +
                                    "/" + location.getMonth() + "/" + location.getYear() +
                                    "\nTimestamp: " + location.getTimestamp();

                            adapter.add(locationInfo);
                        }
                    }
                });

                // Set the date in the view model
                historyViewModel.setDay(date[0]);
                historyViewModel.setMonth(date[1]);
                historyViewModel.setYear(date[2]);
                historyViewModel.setDate("");
            }
        });
    }

    // Function to show the confirmation dialog
    private void showConfirmationDialog(Runnable onConfirmed) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete these locations?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            onConfirmed.run(); // Execute the delete action if the user confirms
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss(); // Dismiss the dialog if the user cancels
        });
        builder.create().show();
    }
}