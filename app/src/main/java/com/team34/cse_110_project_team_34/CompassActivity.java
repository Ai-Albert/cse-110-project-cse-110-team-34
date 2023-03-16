package com.team34.cse_110_project_team_34;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, LocationView> locationsViews;

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

        // Setting up services
        userRepo = new UserRepository(Database.getInstance(this).getUserDao());
        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        // Main user's uid info
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        main_public_uid = preferences.getString("Public", "");
        main_private_uid = preferences.getString("Private", "");

        //checking when the list of users is being updated
        LocationViewModel viewModel = setupViewModel();
        users = viewModel.getUsers();
        users.observe(this, this::updateFriendLocations);
        locationsViews = new HashMap<>();

        TextView public_uid_text = this.findViewById(R.id.public_uid);
        public_uid_text.setText("Public UID: " + preferences.getString("Public", ""));

        // Setting up location/orientation for user
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

    private void updateFriendLocations(List<User> users) {
        if (users == null) return;

        ConstraintLayout cl = this.findViewById(R.id.mainLayout);

        for (User user : users) {
            if (!locationsViews.containsKey(user.public_code)) {
                LocationView newLocation = addLocationView(cl, user);
                locationsViews.put(user.public_code, newLocation);
            }
            LocationView locationView = locationsViews.get(user.public_code);
            updateCompassLocation(user, locationView);
        }
    }

    public void updateCompassLocation(User user, LocationView locationView) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) locationView.itemView.getLayoutParams();
        float azimuth = Calculation.getAngle(lastMainLat, lastMainLong, user.latitude, user.longitude);
        layoutParams.circleAngle = compass.getRotation() + azimuth;
        locationView.itemView.setLayoutParams(layoutParams);
    }

    public void observeOrientation() {
        orientationService.getOrientation().observe(this, orientation -> {
            float newOrientation = Calculation.getCompassRotation(orientation);
            if (Math.abs(compass.getRotation() - newOrientation) % 360 >= 1) {
                compass.setRotation(newOrientation);
            }
            updateFriendLocations(users.getValue());
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
            updateFriendLocations(users.getValue());
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
            updateFriendLocations(users.getValue());
        }
    }

    /**
     * @ensure radius = radius@pre + 5
     * @ensure friend locations on compass are updated according to new radius
     */
    public void onZoomOut(View view) {
        this.radius *= 1.5;
        updateFriendLocations(users.getValue());
    }

    public LocationView addLocationView(ConstraintLayout cl, User user) {
        View inflater = LayoutInflater.from(this)
                .inflate(R.layout.location, cl, false);
        LocationView userView = new LocationView(user, inflater);
        setConstraints(userView);

        cl.addView(inflater);
        return userView;
    }

    private void setConstraints(LocationView userView) {
        // Setting size for ConstraintLayout parent view
        ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userView.itemView.setLayoutParams(constraintLayoutParams);

        // Setting size for TextView name
        userView.nameView.setTextSize(18);
        ConstraintLayout.LayoutParams nameParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        userView.nameView.setLayoutParams(nameParams);

        // Setting size for ImageView status
        ConstraintLayout.LayoutParams statusParams = new ConstraintLayout.LayoutParams(22, 22);
        userView.statusView.setLayoutParams(statusParams);

        // Adding constraints
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) userView.itemView);

        // Constraints for location name
        constraintSet.connect(userView.nameView.getId(), ConstraintSet.TOP,
                userView.itemView.getId(), ConstraintSet.TOP);

        // Constraints for status indicator
        constraintSet.centerHorizontally(userView.statusView.getId(), userView.nameView.getId());
        constraintSet.connect(userView.statusView.getId(), ConstraintSet.TOP,
                userView.nameView.getId(), ConstraintSet.BOTTOM);

        constraintSet.applyTo((ConstraintLayout) userView.itemView);
    }
}