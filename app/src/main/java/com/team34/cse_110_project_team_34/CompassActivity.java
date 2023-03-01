//package com.team34.cse_110_project_team_34;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.constraintlayout.widget.ConstraintSet;
//import androidx.core.util.Pair;
//import androidx.lifecycle.LiveData;
//
//import android.content.Intent;
//import android.content.res.Resources;
//import android.location.GpsStatus;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.List;
//
//import utilities.Calculation;
//import utilities.Coordinate;
//import utilities.CoordinateDao;
//import utilities.Database;
//import utilities.LocationService;
//import utilities.OrientationService;
//
//public class CompassActivity extends AppCompatActivity {
//
//    private CoordinateDao coordinateDao;
//    private OrientationService orientationService;
//    private LocationService locationService;
//
//    private List<Coordinate> locations;
//    final int[] location_ids = {R.id.location_1, R.id.location_2, R.id.location_3};
//
//    private double lastUserLat;
//    private double lastUserLong;
//
//    private ImageView compass;
//
//    public static int getScreenWidth() {
//        return Resources.getSystem().getDisplayMetrics().widthPixels;
//    }
//
//    public static int getScreenHeight() {
//        return Resources.getSystem().getDisplayMetrics().heightPixels;
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_compass);
//
//        coordinateDao = Database.getInstance(this).getCoordinateDao();
//        orientationService = OrientationService.getInstance(this);
//        locationService = LocationService.getInstance(this);
//
//        compass = findViewById(R.id.compass);
//
//        lastUserLat = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().first : 0;
//        lastUserLong = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().second : 0;
//
//        for (int location: location_ids) {
//            TextView location_view = findViewById(location);
//            location_view.setVisibility(View.INVISIBLE);
//            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) location_view.getLayoutParams();
//            layoutParams.circleRadius = (Math.min(getScreenHeight(), getScreenWidth()) / 2) - 100;
//            location_view.setLayoutParams(layoutParams);
//        }
//
//        observeLocation();
//        observeOrientation();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        orientationService.unregisterSensorListeners();
//        locationService.unregisterLocationListener();
//    }
//
//    public void updatePerimeter() {
//        locations = coordinateDao.getAll();
//        int location_number = 0;
//        for (Coordinate location: locations) {
//            TextView location_view = findViewById(location_ids[location_number]);
//            location_view.setVisibility(View.VISIBLE);
//            location_view.setText(location.getLabel());
//
//            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) location_view.getLayoutParams();
//            float azimuth = Calculation.getAngle(lastUserLat, lastUserLong, location.latitude, location.longitude);
//            layoutParams.circleAngle = compass.getRotation() + azimuth;
//            location_view.setLayoutParams(layoutParams);
//
//            location_number++;
//        }
//    }
//
//    public void observeOrientation() {
//        orientationService.getOrientation().observe(this, orientation -> {
//            float newOrientation = Calculation.getCompassRotation(orientation);
//            if (Math.abs(compass.getRotation() - newOrientation) % 360 >= 1) {
//                compass.setRotation(newOrientation);
//            }
//            updatePerimeter();
//        });
//    }
//
//    public void observeLocation() {
//        locationService.getLocation().observe(this, location -> {
//            lastUserLat = location.first;
//            lastUserLong = location.second;
//            updatePerimeter();
//        });
//    }
//
//    public void onAdd(View view) {
//        Intent intent = new Intent(this, AddActivity.class);
//        startActivity(intent);
//    }
//}