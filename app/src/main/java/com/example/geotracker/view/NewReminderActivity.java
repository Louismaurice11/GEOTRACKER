package com.example.geotracker.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityNewReminderBinding;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.model.ReminderEntity;
import com.example.geotracker.view_model.NewReminderViewModel;

// Activity for creating reminders / pings
public class NewReminderActivity extends AppCompatActivity {
    private static final String LOG_TAG = NewReminderViewModel.class.getSimpleName(); // Log tag

    private NewReminderViewModel newReminderViewModel; // View model for this activity

    private ActivityResultLauncher<Intent> launcher; // Activity result launcher

    // Called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        newReminderViewModel = new NewReminderViewModel(getApplication());

        // Set up data binding
        ActivityNewReminderBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_reminder);
        newReminderViewModel = new ViewModelProvider(this).get(NewReminderViewModel.class);
        binding.setViewModel(newReminderViewModel);
        binding.setLifecycleOwner(this);

        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        EditText latitudeEditText = findViewById(R.id.latitudeEditText);
        EditText longitudeEditText = findViewById(R.id.longitudeEditText);

        // Set up save button
        findViewById(R.id.saveBtn).setOnClickListener(v -> {
            String latitude = latitudeEditText.getText().toString();
            String longitude = longitudeEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            if (latitude.isEmpty() || longitude.isEmpty() || description.isEmpty()) {
                showEmptyFieldsDialog();
                return;
            }
            ReminderEntity reminder = new ReminderEntity(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude),
                description);
            newReminderViewModel.insertReminder(reminder);
            finish();
        });
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        // Initialize the ActivityResultLauncher
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Handle the result here
                        Intent data = result.getData();
                        if (data != null) {
                            newReminderViewModel.setLocation(data.getDoubleExtra("latitude", 0),
                                    data.getDoubleExtra("longitude", 0));
                            Log.d(LOG_TAG, "onCreate: " + data.getDoubleExtra("latitude", 0) + " " + data.getDoubleExtra("longitude", 0));
                            latitudeEditText.setText(String.valueOf(data.getDoubleExtra("latitude", 0)));
                            longitudeEditText.setText(String.valueOf(data.getDoubleExtra("longitude", 0)));
                        }
                    }
                });

        // set up map button
        findViewById(R.id.mapBtn).setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString();
            if(description.isEmpty()){
                // Create dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Incomplete Information");
                builder.setMessage("Please enter the title before placing a ping.");

                // Set up OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                Intent intent = new Intent(this, PlacePingActivity.class);
                launcher.launch(intent);
            }
        });

    }

    // Show dialog if fields are empty
    private void showEmptyFieldsDialog() {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incomplete Information");
        builder.setMessage("Please fill in all fields to save the reminder.");

        // Set up OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}