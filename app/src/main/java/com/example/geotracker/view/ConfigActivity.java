package com.example.geotracker.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityConfigBinding;
import com.example.geotracker.databinding.ActivityHistoryBinding;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.view_model.ConfigViewModel;
import com.example.geotracker.view_model.HistoryViewModel;

// Activity for configuring the app
public class ConfigActivity extends AppCompatActivity {
    private static final String LOG_TAG = ConfigActivity.class.getSimpleName(); // Log tag

    private ConfigViewModel configViewModel; // View model for this activity

    // Config settings
    private String selectedMeasurement; // The selected measurement system
    private SharedPreferences sharedPreferences; // Shared preferences

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Set up shared preferences
        sharedPreferences = getSharedPreferences("com.example.geotracker", Context.MODE_PRIVATE);
        selectedMeasurement = sharedPreferences.getString("measurement_system", "metric");
        float userHeight = sharedPreferences.getFloat("user_height", 1.7f);
        Log.d(LOG_TAG, "Selected measurement system: " + selectedMeasurement + ", user height: " + userHeight);

        // Set up data binding
        ActivityConfigBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_config);
        configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        binding.setViewModel(configViewModel);
        binding.setLifecycleOwner(this);

        // Set up the EditText for user's height and convert to String and set as text
        EditText heightEditText = findViewById(R.id.heightEditText);
        heightEditText.setText(String.valueOf(userHeight));

        // Set up radio group
        RadioGroup radioGroup = findViewById(R.id.measurementSystemRadioGroup);

        // Check the appropriate radio button based on the stored preference
        if (selectedMeasurement.equals("metric")) { // If metric
            // Check the metric radio button
            radioGroup.check(R.id.metricRadioButton);
        } else if (selectedMeasurement.equals("imperial")) { // If imperial
            // Check the imperial radio button
            radioGroup.check(R.id.imperialRadioButton);
        }

        // Set up listener for radio group
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Handle the checked radio button here
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) { // If the selected radio button is not null
                // Set the selected measurement system
                selectedMeasurement = selectedRadioButton.getText().toString().toLowerCase();
            }
        });

        // Set up onClick listener for the save data button
        findViewById(R.id.saveDataBtn).setOnClickListener(v -> {
            // Edit the SharedPreferences to save the users height and measurement system
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("user_height", Float.parseFloat(heightEditText.getText().toString().toLowerCase()));
            editor.putString("measurement_system", selectedMeasurement);

            // Commit the changes
            editor.apply();
        });

        // Set up navigation bar
        NavBarConstructor.addOnClick(findViewById(R.id.homeBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.trackerBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.historyBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.pingBtn), this);
    }
}