package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import utilities.LocationService;
import utilities.OrientationService;

public class CompassActivity extends AppCompatActivity {

    private OrientationService orientationService;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        this.observeOrientation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView compass = findViewById(R.id.compass);
        orientationService.getOrientation().observe(this, orientation -> {
            compass.setRotation(360 - (float) Math.toDegrees(orientation));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void observeOrientation() {
        ImageView compass = findViewById(R.id.compass);
        orientationService.getOrientation().observe(this, orientation -> {
            compass.setRotation(360 - (float) Math.toDegrees(orientation));
        });
    }
}