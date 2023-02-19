package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import utilities.Coordinate;
import utilities.CoordinateDao;
import utilities.Database;

public class AddActivity extends AppCompatActivity {
    private CoordinateDao coordinateDao;

    private int remainingLocations;

    private EditText coordinates;
    private EditText location_name;
    private TextView remain;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        coordinates = findViewById(R.id.Coordinates);
        remain = findViewById(R.id.remaining);
        location_name = findViewById(R.id.LocationName);
        skip = findViewById(R.id.skip);

        coordinateDao = Database.getInstance(this).getCoordinateDao();
        remainingLocations = 3 - coordinateDao.getAll().size();
        remain.setText("You have " + remainingLocations + " locations left.");

        if (remainingLocations == 3) {
            skip.setVisibility(View.INVISIBLE);
            skip.setClickable(false);
        }
    }

    public boolean verifyCoordinate(String[] coordinateString) {
        if (coordinateString.length != 2) {
            coordinates.setError("Coordinates should be in the format (latitude, longitude)");
            return false;
        }
        try {
            Double.parseDouble(coordinateString[0]);
            Double.parseDouble(coordinateString[1]);
        } catch (NumberFormatException e) {
            coordinates.setError("Coordinates should be numbers.");
            return false;
        }
        return true;
    }

    public void onSkip(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void onSubmit(View view) {
        if (remainingLocations <= 0) {
            return;
        }

        String[] coords = coordinates.getText().toString().split(", ");

        if (!verifyCoordinate(coords)) {
            return;
        }
        double latitude = Double.parseDouble(coords[0]);
        double longitude = Double.parseDouble(coords[1]);

        String place_name = location_name.getText().toString();
        Coordinate new_coordinate = new Coordinate(place_name, latitude, longitude);
        coordinateDao.insert(new_coordinate);

        remainingLocations--;
        remain.setText("You have " + remainingLocations + " locations left.");

        skip.setVisibility(View.VISIBLE);
        skip.setClickable(true);

        location_name.setText("");
        coordinates.setText("");

        if (remainingLocations == 0) {
            Intent intent = new Intent(this, CompassActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Database.getInstance(this).close();
        super.onDestroy();
    }

}
