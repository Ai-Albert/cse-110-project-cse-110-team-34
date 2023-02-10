package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import utilities.Coordinate;
import utilities.CoordinateDao;
import utilities.Database;

public class MainActivity extends AppCompatActivity {

    private CoordinateDao coordinateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database database = Room.inMemoryDatabaseBuilder(getApplicationContext(), Database.class).build();
        coordinateDao = database.getCoordinateDao();
        TextView remain = (TextView) findViewById(R.id.remaining);
//        int remaining_locations = 3 - coordinateDao.getAll().size();
        remain.setText("You have " + "_remaining_locations_" + "locations left.");
    }

    public void onSubmit(View view) {
        int size = coordinateDao.getAll().size();
        if (size >= 3) {
            return;
        }

        EditText coordinates = (EditText) findViewById(R.id.Coordinates);
        EditText location_name = (EditText) findViewById(R.id.LocationName);

        String[] coords = coordinates.getText().toString().split(", ");
        System.out.println(coords.length);
        if (coords.length != 2) {
            coordinates.setError("Coordinates should be in the format (latitude, longitude)");
            return;
        }
        double latitude;
        double longitude;
        try {
            latitude = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
        } catch (NumberFormatException e) {
            coordinates.setError("Coordinates should be numbers.");
            return;
        }
        String place_name = location_name.getText().toString();
        Coordinate new_coordinate = new Coordinate(place_name, latitude, longitude);

        coordinateDao.insert(new_coordinate);
//
//        int remaining_locations = 3 - size;
//        TextView remain = (TextView) findViewById(R.id.remaining);
//        remain.setText("You have " + remaining_locations + "locations left.");
    }
}