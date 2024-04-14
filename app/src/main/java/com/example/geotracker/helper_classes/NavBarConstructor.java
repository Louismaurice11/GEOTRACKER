package com.example.geotracker.helper_classes;


import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geotracker.view.ConfigActivity;
import com.example.geotracker.view.HistoryActivity;
import com.example.geotracker.view.NewReminderActivity;
import com.example.geotracker.view.ReminderActivity;
import com.example.geotracker.view.TrackerActivity;

// Helper class to construct the navigation bar
public class NavBarConstructor {

    // Private constructor to prevent instantiation
    private NavBarConstructor() {
        // Empty constructor
    }

    // Add onClick listeners to the navigation bar buttons
    public static void addOnClick(Button btn, AppCompatActivity activity) {
        String id = btn.getResources().getResourceName(btn.getId());
        // Set up onClick listeners
        switch (id) {
            case "com.example.geotracker:id/trackerBtn": // Tracker button
                btn.setOnClickListener(v -> {
                    // Start the tracker activity
                    Intent intent = new Intent(activity, TrackerActivity.class);
                    activity.startActivity(intent);

                    // Finish the current activity if it is not the main activity
                    if (!activity.getClass().getSimpleName().equals("MainActivity")) {
                        activity.finish();
                    }
                });
                break;
            case "com.example.geotracker:id/pingBtn": // Ping button
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, ReminderActivity.class);
                    activity.startActivity(intent);

                    // Finish the current activity if it is not the main activity
                    if (!activity.getClass().getSimpleName().equals("MainActivity")) {
                        activity.finish();
                    }
                });
                break;
            case "com.example.geotracker:id/homeBtn": // Home button
                btn.setOnClickListener(v -> {
                    activity.finish();
                });
                break;
            case "com.example.geotracker:id/historyBtn": // History button
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, HistoryActivity.class);
                    activity.startActivity(intent);

                    // Finish the current activity if it is not the main activity
                    if (!activity.getClass().getSimpleName().equals("MainActivity")) {
                        activity.finish();
                    }
                });
                break;
            case "com.example.geotracker:id/settingsBtn": // Settings button
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, ConfigActivity.class);
                    activity.startActivity(intent);

                    // Finish the current activity if it is not the main activity
                    if (!activity.getClass().getSimpleName().equals("MainActivity")) {
                        activity.finish();
                    }
                });
                break;
            default:
                break;
        }
    }
}
