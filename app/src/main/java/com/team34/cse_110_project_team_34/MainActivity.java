package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import utilities.CoordinateDao;
import utilities.Database;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoordinateDao coordinateDao = Database.getInstance(this).getCoordinateDao();
        if (coordinateDao.getAll().size() == 0) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, CompassActivity.class);
            startActivity(intent);
        }
    }
}