package com.example.geotracker.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.geotracker.R;
import com.example.geotracker.adapters.ReminderAdapter;
import com.example.geotracker.helper_classes.NavBarConstructor;
import com.example.geotracker.model.ReminderEntity;
import com.example.geotracker.view_model.ReminderViewModel;

// Activity for viewing reminders / pings
public class ReminderActivity extends AppCompatActivity implements ReminderAdapter.OnItemClickListener{
    private static final String LOG_TAG = ReminderActivity.class.getSimpleName(); // Log tag

    private ReminderViewModel reminderViewModel; // View model for this activity
    private ReminderAdapter reminderAdapter; // Adapter for the recycler view

    // Called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Set up navigation bar
        NavBarConstructor.addOnClick(findViewById(R.id.homeBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.trackerBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.historyBtn), this);
        NavBarConstructor.addOnClick(findViewById(R.id.settingsBtn), this);

        // Set up recycler view
        RecyclerView recyclerView = findViewById(R.id.reminderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter();
        recyclerView.setAdapter(reminderAdapter);

        // Set up on click listeners
        reminderAdapter.setOnItemClickListener(this);
        Button newReminderBtn = findViewById(R.id.newPingBtn);
        newReminderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ReminderActivity.this, NewReminderActivity.class);
            startActivity(intent);
        });

        // Set up view model
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        reminderViewModel.getAllReminders().observe(this, reminderEntities -> {
            if (reminderEntities != null && !reminderEntities.isEmpty()) {
                reminderAdapter.setReminders(reminderEntities);
            }
        });
    }

    // Called when an item in the recycler view is clicked
    @Override
    public void onItemClick(ReminderEntity reminder) {
        showDeleteConfirmationDialog(reminder);
    }

    // Show a dialog to confirm deletion of a reminder
    private void showDeleteConfirmationDialog(final ReminderEntity reminder) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Reminder");
        builder.setMessage("Are you sure you want to delete this reminder?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Delete the reminder from the database or your data source
            reminderViewModel.deleteReminderById(reminder.getId());
            reminderAdapter.notifyDataSetChanged();
            Intent intent = new Intent(ReminderActivity.this, ReminderActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up the cancel button
        builder.setNegativeButton("Cancel", null);

        // Create and show the AlertDialog
        builder.show();
    }
}