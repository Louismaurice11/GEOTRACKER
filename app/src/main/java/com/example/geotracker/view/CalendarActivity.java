package com.example.geotracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.geotracker.R;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    private static final String LOG_TAG = CalendarActivity.class.getSimpleName(); // Log tag

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Get the data from the previous activity's bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(LOG_TAG, "Extras not null");
            // Get the data from the bundle
            int measureOption = extras.getInt("measureOption");
            float averageDistance = extras.getFloat("averageDistance");
            String distanceUnit = extras.getString("distanceUnit");

            // Set up calendar
            CalendarView calendarView = findViewById(R.id.calendarView);
            calendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);
            calendarView.setOnDateChangeListener((view, year, month, day) -> {
                // Get the current date
                Calendar currentDate = Calendar.getInstance();
                int currentYear = currentDate.get(Calendar.YEAR);
                int currentMonth = currentDate.get(Calendar.MONTH);
                int currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

                // Check if the selected date is not in the future
                if ((year < currentYear) || (year == currentYear && month < currentMonth) ||
                        (year == currentYear && month == currentMonth && day <= currentDayOfMonth)) {
                    // Date is not in the future, proceed to JournalActivity
                    Intent intent = new Intent(CalendarActivity.this, AnnotationActivity.class);
                    Bundle bundle = new Bundle();

                    // Add the data to the bundle
                    bundle.putInt("year", year);
                    bundle.putInt("month", month+1);
                    bundle.putInt("day", day);
                    bundle.putInt("measureOption", measureOption);
                    bundle.putFloat("averageDistance", averageDistance);
                    bundle.putString("distanceUnit", distanceUnit);

                    // Add the bundle to the intent and start the activity
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });


            // Set up back button
            Button backBtn = findViewById(R.id.backBtn);
            backBtn.setOnClickListener(v -> {
                finish();
            });
        } else{
            Log.d(LOG_TAG, "Extras null");
        }
    }
}