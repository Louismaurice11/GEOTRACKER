package com.example.geotracker.OSM;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.geotracker.R;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class PingInfoWindow extends InfoWindow {
    public PingInfoWindow(int layoutResId, org.osmdroid.views.MapView mapView) {
        super(layoutResId, mapView);
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        View view = mView.findViewById(R.id.custom_info_window);
        TextView titleTextView = view.findViewById(R.id.titleTextView);

        // Set the title and other details here
        titleTextView.setText(marker.getTitle());

        // Find the close button and set its click listener
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    @Override
    public void onClose() {

    }
}
