package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import database.Database;
import database.UserDao;
import model.User;
import utilities.Calculation;
import utilities.LocationService;
import utilities.OrientationService;

public class CompassActivity extends AppCompatActivity {

    private UserDao userDao;
    private OrientationService orientationService;
    private LocationService locationService;

    private double lastMainLat;
    private double lastMainLong;
    private List<User> friends;
    private double radius; // Miles

    private ImageView compass;

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

        userDao = Database.getInstance(this).getUserDao();
        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        compass = findViewById(R.id.compass);
        radius = 50;

        lastMainLat = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().first : 0;
        lastMainLong = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().second : 0;

        observeLocation();
        observeOrientation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void updateFriendLocations() {
        //
    }

    public void observeOrientation() {
        orientationService.getOrientation().observe(this, orientation -> {
            float newOrientation = Calculation.getCompassRotation(orientation);
            if (Math.abs(compass.getRotation() - newOrientation) % 360 >= 1) {
                compass.setRotation(newOrientation);
            }
            updateFriendLocations();
        });
    }

    public void observeLocation() {
        locationService.getLocation().observe(this, location -> {
            lastMainLat = location.first;
            lastMainLong = location.second;
            updateFriendLocations();
        });
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, NewFriendActivity.class);
        startActivity(intent);
    }
    
    public void onZoomOut(View view) {
        this.radius += 5;
        updateFriendLocations();
    }
}