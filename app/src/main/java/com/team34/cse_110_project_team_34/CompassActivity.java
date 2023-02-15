package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import utilities.OrientationService;

public class CompassActivity extends AppCompatActivity {
    private OrientationService orientationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        orientationService = new OrientationService();

    }
}