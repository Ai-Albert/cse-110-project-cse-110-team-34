package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import utilities.Coordinate;
import utilities.CoordinateDao;
import utilities.Database;
import utilities.LocationService;
import utilities.OrientationService;

public class CompassActivity extends AppCompatActivity {

    private OrientationService orientationService;
    private LocationService locationService;

    private CoordinateDao coordinateDao;

    private List<Coordinate> locations;

    private ImageView compass;
    final int[] location_ids = {R.id.location_1, R.id.location_2, R.id.location_3};

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        coordinateDao = Database.getInstance(this).getCoordinateDao();
        locations = coordinateDao.getAll();

        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        compass = findViewById(R.id.compass);

        for (int location: location_ids) {
            ImageView location_view = findViewById(location);
            location_view.setVisibility(View.INVISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) location_view.getLayoutParams();
            layoutParams.circleRadius = (Math.min(getScreenHeight(), getScreenWidth()) / 2) - 100;
            location_view.setLayoutParams(layoutParams);
        }

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
        observeOrientation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void observeOrientation() {
        orientationService.getOrientation().observe(this, orientation -> {
            compass.setRotation(360 - (float) Math.toDegrees(orientation));
        });
        int location_number = 0;
        for (Coordinate location: locations) {
            ImageView location_view = findViewById(location_ids[location_number]);
            location_view.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) location_view.getLayoutParams();
            layoutParams.circleAngle = 120 * location_number;
            location_view.setLayoutParams(layoutParams);
            location_number++;
        }
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }
}