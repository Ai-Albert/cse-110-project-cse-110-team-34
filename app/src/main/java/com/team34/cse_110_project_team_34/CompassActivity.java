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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import database.Database;
import database.UserDao;
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

        UserDao dao = Database.getInstance(this).getUserDao();

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        String link = preferences.getString("API_Link", "");
        if (link.equals("")) {
            userRepo = new UserRepository(dao);
        } else {
            userRepo = new UserRepository(dao, link);
        }

        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        main_public_uid = preferences.getString("Public", "");
        main_private_uid = preferences.getString("Private", "");

        //checking when the list of users is being updated
        LocationViewModel viewModel = setupViewModel();
        users = viewModel.getUsers();
        users.observe(this, this::onUsersChanged);
        locationsViews = new HashMap<>();

        TextView public_uid_text = this.findViewById(R.id.public_uid);
        public_uid_text.setText("Public UID: " + preferences.getString("Public", ""));

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
        ConstraintLayout cl = this.findViewById(R.id.constraint_main);

        for (User user : users) {
            if (user.public_code.equals(main_public_uid)) {
                continue;
            }
            if (!locationsViews.containsKey(user.public_code)) {
                LocationView newLocation = addLocationView(cl, user);
                locationsViews.put(user.public_code, newLocation);
            }
        }
    }

    public void updateFriendLocations() {
        // TODO: use last fetched friend users lat/long to calculate radius and angle for compass placement
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

    public LocationView addLocationView(ConstraintLayout cl, User user) {
        View inflater = LayoutInflater.from(this)
                .inflate(R.layout.location, cl, false);
        LocationView userView = new LocationView(user, inflater);
        cl.addView(inflater);
        return userView;
    }
}