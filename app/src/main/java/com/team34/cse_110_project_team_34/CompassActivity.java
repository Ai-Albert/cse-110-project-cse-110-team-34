package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import utilities.OrientationService;

public class CompassActivity extends AppCompatActivity {

    private OrientationService orientationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        orientationService = new OrientationService(this);
        ImageView compass = findViewById(R.id.compass);

        orientationService.getOrientation().observe(this, orientation -> {
            compass.setRotation(360 - (float) Math.toDegrees(orientation));
        });
    }
}