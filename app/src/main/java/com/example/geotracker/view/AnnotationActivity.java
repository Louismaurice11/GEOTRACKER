package com.example.geotracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.geotracker.R;
import com.example.geotracker.databinding.ActivityAnnotationBinding;
import com.example.geotracker.model.AnnotationEntity;
import com.example.geotracker.view_model.AnnotationViewModel;

import java.util.List;

// Activity for adding annotations to specific dates
public class AnnotationActivity extends AppCompatActivity {
    public static final String LOG_TAG = AnnotationActivity.class.getSimpleName(); // Log tag

    // String constants
    public static final String BEAT_AVERAGE = "You beat your average distance!";
    public static final String DID_NOT_BEAT_AVERAGE = "You did not beat your average distance.";

    private AnnotationViewModel annotationViewModel; // View model for this activity
    private SharedPreferences sharedPreferences; // Shared preferences


    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        // Set up data binding
        ActivityAnnotationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_annotation);
        annotationViewModel = new ViewModelProvider(this).get(AnnotationViewModel.class);
        binding.setViewModel(annotationViewModel);
        binding.setLifecycleOwner(this);

        // Set up shared preferences
        sharedPreferences = getSharedPreferences("com.example.geotracker", Context.MODE_PRIVATE);
        String selectedMeasurement = sharedPreferences.getString("measurement_system", "metric");
        float userHeight = sharedPreferences.getFloat("user_height", 1.7f);


        // Get the bundle from the intent
        Bundle extras = getIntent().getExtras();

        // Get measure option and distance unit from bundle
        int measureOption = extras.getInt("measureOption");
        String distanceUnit = extras.getString("distanceUnit");

        // Get date from the bundle
        int year = extras.getInt("year");
        int month = extras.getInt("month");
        int day = extras.getInt("day");

        Log.d(LOG_TAG, "Year: " + year + ", Month: " + month + ", Day: " + day);

        annotationViewModel.setDate(year, month, day, measureOption, userHeight, selectedMeasurement);
        annotationViewModel.setDistanceUnit(distanceUnit);

        // Observe distance changes
        annotationViewModel.getDistance().observe(this, distance -> {
            // Mark whether user has beat their average distance
            float averageDistance = getIntent().getFloatExtra("averageDistance", 0.0f);
            TextView annotationText = findViewById(R.id.goalTxtView);

            // Set the text depending on whether the user has beat their average distance
            if(annotationViewModel.getDistance().getValue() > averageDistance) { // If user has beat their average distance
                annotationText.setText(BEAT_AVERAGE);
            } else { // If user has not beat their average distance
                annotationText.setText(DID_NOT_BEAT_AVERAGE);
            }

        });

        // Set up back button
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            // Create a new annotation
            AnnotationEntity newAnnotation = new AnnotationEntity(day, month, year,
                    annotationViewModel.getAnnotation().getValue(),
                    annotationViewModel.getRating().getValue());

            // Check if there are any annotations for this date
            boolean empty = annotationViewModel.getAnnotation().getValue().equals("");
            if(annotationViewModel.getNumAnnotations() == 0 && !empty) { // If there are no annotations for this date
                // Insert the annotation into the database
                annotationViewModel.insertAnnotation(newAnnotation);

            } else { // If there are annotations for this date
                // Delete the annotations for this date and insert the new annotation
                annotationViewModel.deleteAnnotationsFromToday(day, month, year);
                annotationViewModel.insertAnnotation(newAnnotation);
            }
            finish();
        });

        // Set up rating bar
        RatingBar  ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            // Update the rating in the view model
            annotationViewModel.setRating(rating);
        });
    }

    // Called when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the observers from the view model
        annotationViewModel.removeObservers(AnnotationActivity.this);
    }
}