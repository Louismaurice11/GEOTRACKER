package com.example.geotracker.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityMainBinding;
import com.example.geotracker.databinding.ActivityTrackerBinding;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.view_model.MainViewModel;
import com.example.geotracker.view_model.TrackerViewModel;

import java.util.Calendar;

// Distance tracking activity
public class TrackerActivity extends AppCompatActivity {
    public static final String LOG_TAG = TrackerActivity.class.getSimpleName(); // Log tag

    private TrackerViewModel trackerViewModel; // The ViewModel
    private SharedPreferences sharedPreferences; // Shared preferences

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // Set up data binding
        ActivityTrackerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tracker);
        trackerViewModel = new ViewModelProvider(this).get(TrackerViewModel.class);
        binding.setViewModel(trackerViewModel);
        binding.setLifecycleOwner(this);

        // Set up navigation bar
        NavBarConstructor.addOnClick(findViewById(R.id.homeBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.pingBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.historyBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.settingsBtn), this);

        // Set up shared preferences
        sharedPreferences = getSharedPreferences("com.example.geotracker", Context.MODE_PRIVATE);
        String selectedMeasurement = sharedPreferences.getString("measurement_system", "metric");

        // Set up buttons
        Button stepBtn = findViewById(R.id.stepBtn);
        stepBtn.setOnClickListener(v -> {
            trackerViewModel.setMeasureOption(0);
            trackerViewModel.updateUI();
        });
        Button meterBtn = findViewById(R.id.mBtn);
        meterBtn.setOnClickListener(v -> {
            trackerViewModel.setMeasureOption(1);
            trackerViewModel.updateUI();
        });
        Button kmBtn = findViewById(R.id.kMBtn);
        kmBtn.setOnClickListener(v -> {
            trackerViewModel.setMeasureOption(2);
            trackerViewModel.updateUI();
        });

        // Set up button text
        if(selectedMeasurement.equals("metric")) {
            meterBtn.setText(R.string.meter);
            kmBtn.setText(R.string.km);
        } else {
            meterBtn.setText(R.string.yard);
            kmBtn.setText(R.string.mile);
        }

        // Set up calendar button
        Button calendarBtn = findViewById(R.id.calBtn);
        calendarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TrackerActivity.this, CalendarActivity.class);

            // Pass measure option and average distance to CalendarActivity
            Bundle bundle = new Bundle();
            bundle.putInt("measureOption", trackerViewModel.getMeasureOption());
            bundle.putFloat("averageDistance", trackerViewModel.getAverageDistance().getValue());
            bundle.putString("distanceUnit", trackerViewModel.getDistanceUnit().getValue());

            // Pass bundle to CalendarActivity
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}