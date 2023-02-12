package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import utilities.Coordinate;
import utilities.CoordinateDao;
import utilities.Database;

public class MainActivity extends AppCompatActivity {

    private Database database;
    private CoordinateDao coordinateDao;


    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private int remainingLocations;

    private EditText coordinates;
    private EditText location_name;
    private TextView remain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinates = (EditText) findViewById(R.id.Coordinates);
        remain = (TextView) findViewById(R.id.remaining);
        location_name = (EditText) findViewById(R.id.LocationName);

        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), Database.class).build();

        future = backgroundThreadExecutor.submit(() -> {
            coordinateDao = database.getCoordinateDao();
            remainingLocations = 3 - coordinateDao.getAll().size();
            runOnUiThread(() -> {
                remain.setText("You have " + remainingLocations + " locations left.");
            });
            return null;
        });


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
        this.future = backgroundThreadExecutor.submit(() -> {
            coordinateDao.insert(new_coordinate);
            remainingLocations = 3 - coordinateDao.getAll().size();
            runOnUiThread(() -> {
                remain.setText("You have " + remainingLocations + " locations left.");
            });
            return null;
        });
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}