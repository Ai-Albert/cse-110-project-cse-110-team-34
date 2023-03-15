package com.team34.cse_110_project_team_34;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import database.Database;
import database.UserRepository;
import model.User;
import utilities.Calculation;
import utilities.LocationService;
import utilities.OrientationService;
import view.LocationView;
import viewModel.LocationViewModel;

public class CompassActivity extends AppCompatActivity {

    private UserRepository userRepo;
    private OrientationService orientationService;
    private LocationService locationService;

    private String main_public_uid;
    private String main_private_uid;
    private LiveData<List<User>> users;
    private List<LocationView> views;

    private double lastMainLat;
    private double lastMainLong;


    @VisibleForTesting
    public double radius; // Miles

    private ImageView compass;


    // screen width/height used for UI element layout
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

        userRepo = new UserRepository(Database.getInstance(this).getUserDao());
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        main_public_uid = preferences.getString("Public", "");
        main_private_uid = preferences.getString("Private", "");

        //checking when the list of users is being updated
        LocationViewModel viewModel = setupViewModel();
        users = viewModel.getUsers();
        users.observe(this, this::onUsersChanged);

//        ConstraintLayout ll = this.findViewById(R.id.constraint_main);
//        for (User user: users.getValue()) {
//            views.add(addLocationView(ll, user));
//        }
//        setContentView(ll);

        TextView public_uid_text = this.findViewById(R.id.public_uid);
        public_uid_text.setText("Public UID: " + preferences.getString("Public", ""));

        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        compass = findViewById(R.id.compass);
        radius = 20;

        lastMainLat = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().first : 0;
        lastMainLong = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().second : 0;

        observeLocation();
        observeOrientation();
    }

    private LocationViewModel setupViewModel() {
        return new ViewModelProvider(this).get(LocationViewModel.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    private void onUsersChanged(List<User> users) {
        System.out.println("users has been changed");
    }

    public void updateFriendLocations() {
        // TODO: use last fetched friend users lat/long to calculate radius and angle for compass placement
        //Log.d("update", preferences.getString("Private", ""));
//        Log.d("Update", mainUser.getValue().toPatchJSON("aaa"));
//        userRepo.upsertSynced(preferences.getString("Private", ""), mainUser.getValue());
//        List<User> currUsers = users.getValue();
//        for (int i = 0; i < currUsers.size(); i++) {
//            views.get(i).update(currUsers.get(i));
//        }
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

    /**
     * @ensure remote DB is updated with lastMainLat and lastMainLong
     * @ensure friend locations on compass are updated according to new location with existing radius
     */
    public void observeLocation() {
        locationService.getLocation().observe(this, location -> {
            lastMainLat = location.first;
            lastMainLong = location.second;

            User mainUser = userRepo.getLocal(main_public_uid);
            mainUser.setLatitude(lastMainLat);
            mainUser.setLongitude(lastMainLong);

            userRepo.updateSynced(main_private_uid, mainUser);
            updateFriendLocations();
        });
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, NewFriendActivity.class);
        startActivity(intent);
    }

    /**
     * @require radius > 5
     * @ensure radius = radius@pre - 5
     * @ensure friend locations on compass are updated according to new radius
     */
    public void onZoomIn(View view) {
        if (radius >= 13) {
            radius /= 1.5;
            updateFriendLocations();
        }
    }

    /**
     * @ensure radius = radius@pre + 5
     * @ensure friend locations on compass are updated according to new radius
     */
    public void onZoomOut(View view) {
        this.radius *= 1.5;
        updateFriendLocations();
    }

    public LocationView addLocationView(ConstraintLayout ll, User user) {
        View inflater = LayoutInflater.from(this)
                .inflate(R.layout.location, ll, false);

        LocationView userView = new LocationView(user, inflater);

        return userView;
    }
}