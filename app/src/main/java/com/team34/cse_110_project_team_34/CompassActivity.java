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
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    SharedPreferences preferences;
    private LocationViewModel viewModel;

    private String main_public_uid;
    private String main_private_uid;
    private LiveData<List<User>> users;
    private List<User> usersList;
    private List<LiveData<User>> userDatas;
    @VisibleForTesting
    public Map<String, LocationView> locationViews;
    private List<ImageView> circleViews;

    private double lastMainLat;
    private double lastMainLong;

    @VisibleForTesting
    public int radius; // Miles
    public final int[] radii = {0, 1, 10, 500, Integer.MAX_VALUE};
    public int radiusIndex;

    private final double COMPASS_EDGE = (getScreenWidth() - 32) / 2.0;

    private ImageView compass;
    private ConstraintLayout compassLayout;

    // screen width/height used for UI element layout
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        // Setting up services
        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        // Main user's uid info
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        main_public_uid = preferences.getString("Public", "");
        main_private_uid = preferences.getString("Private", "");

        // API mocking
        UserDao dao = Database.getInstance(this).getUserDao();
        String link = preferences.getString("API_Link", "");
        if (link.equals("")) {
            userRepo = new UserRepository(dao);
        } else {
            userRepo = new UserRepository(dao, link);
        }

        // Getting the current user's public uid
        TextView public_uid_text = this.findViewById(R.id.public_uid);
        public_uid_text.setText(String.format("%s%s", getString(R.string.publicUIDString), preferences.getString("Public", "")));

        // Setting up location/orientation for user
        compass = findViewById(R.id.compass);
        compassLayout = findViewById(R.id.compassLayout);
        circleViews = new ArrayList<>();
        radius = 20;

        // Checking when the list of users is being updated
        viewModel = setupViewModel();
        users = viewModel.getUsers();
        users.observe(this, this::updateFriendLocations);
        usersList = viewModel.getUsersNotLive();
        userDatas = new ArrayList<>();
        locationViews = new HashMap<>();

        // Setting up compass
        compass = findViewById(R.id.compass);
        compassLayout = findViewById(R.id.compassLayout);
        circleViews = new ArrayList<>();
        radius = preferences.getInt("Radius", 10);
        radiusIndex = preferences.getInt("Index", 2);
        if (radiusIndex == 0) {
            setNotClickable(findViewById(R.id.zoomInButton));
        }
        else if (radiusIndex == 4) {
            setNotClickable(findViewById(R.id.zoomOutButton));
        }

        // Setting up location/orientation for user
        lastMainLat = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().first : 0;
        lastMainLong = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().second : 0;

        setupObservers();
        updateCircles();
        observeLocation();
        observeOrientation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupObservers();
    }

    private LocationViewModel setupViewModel() {
        return new ViewModelProvider(this).get(LocationViewModel.class);
    }

    private void setupObservers() {
        for (User user : usersList) {
            if (user.public_code.equals(main_public_uid)) {
                continue;
            }
            LiveData<User> liveUser = userRepo.getSynced(user.public_code);
            userDatas.add(liveUser);
            liveUser.observe(this, this::updateCompassLocation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    /**
     * We recalculate each friend's position on our compass, creating LocationViews for them if
     * they didn't already exist.
     *
     * @param users The complete list of friends
     */
    private void updateFriendLocations(List<User> users) {
        if (users == null) {
            return;
        }

        ConstraintLayout cl = this.findViewById(R.id.mainLayout);
        for (User user : users) {
            if (!locationViews.containsKey(user.public_code)) {
                LocationView newLocation = addLocationView(cl, user);
                locationViews.put(user.public_code, newLocation);
            }
            locationViews.get(user.public_code).update(user);
            updateCompassLocation(user);
        }
    }

    /**
     * Displays a user's location marker on the compass.
     *
     * @param user The user whose location we are displaying
     */
    public void updateCompassLocation(User user) {
        LocationView userView = locationViews.get(user.public_code);
        assert userView != null;

        // Getting angle
        float azimuth = compass.getRotation() + Calculation.getAngle(lastMainLat, lastMainLong, user.latitude, user.longitude);

        // Getting radius
        float distance = Calculation.getDistance(lastMainLat, lastMainLong, user.latitude, user.longitude);
        int lowerIndex = 0;
        for (int i = 1; i < 5; i++) {
            if (distance >= radii[i]) {
                lowerIndex = i;
            }
        }

        int compassRadius = 0;
        userView.nameView.setText(user.name);
        if (lowerIndex >= radiusIndex) {
            compassRadius = (int) COMPASS_EDGE;
            userView.nameView.setText("");
        }
        else {
            int lowerRadius = lowerIndex == 0 ? 0 : ((ConstraintLayout.LayoutParams) circleViews.get(lowerIndex - 1).getLayoutParams()).width / 2;
            int upperRadius = ((ConstraintLayout.LayoutParams) circleViews.get(lowerIndex).getLayoutParams()).width / 2;
            compassRadius = (lowerRadius - upperRadius) / 2 + upperRadius;
        }

        // Checking for main user
        if (user.public_code.equals(main_public_uid)) {
            compassRadius = -30;
            azimuth = 0;
        }

        // Setting new constraint based on angle and radius
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) findViewById(R.id.mainLayout));
        constraintSet.constrainCircle(userView.itemView.getId(), compassLayout.getId(), compassRadius, azimuth);
        constraintSet.applyTo(findViewById(R.id.mainLayout));
        userView.itemView.bringToFront();
    }

    /**
     * Draws equidistant concentric circles based on the current zoom level.
     */
    public void updateCircles() {
        ConstraintLayout cl = findViewById(R.id.mainLayout);

        for (ImageView circle : circleViews) {
            cl.removeView(circle);
        }
        circleViews.clear();

        int currIndex = 1;
        while (currIndex <= radiusIndex) {
            drawCircle(currIndex, radiusIndex);
            currIndex++;
        }
    }

    /**
     * Draws a circle for a certain distance based on the current zoom level.
     *
     * @param currIndex The index for the distance of the circle in radii
     * @param radiusIndex The index of our current zoom level
     */
    public void drawCircle(int currIndex, int radiusIndex) {
        ConstraintLayout cl = findViewById(R.id.mainLayout);

        ImageView circle = new ImageView(this);
        circle.setId(View.generateViewId());
        circle.setImageResource(R.drawable.circle_1);
        cl.addView(circle);
        circleViews.add(circle);

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) circle.getLayoutParams();
        int trueRadius = (int) (((float) currIndex / radiusIndex) * COMPASS_EDGE);
        lp.width = (int) (trueRadius * 2);
        lp.height = (int) (trueRadius * 2);
        circle.setLayoutParams(lp);

        ConstraintSet cs = new ConstraintSet();
        cs.clone(cl);
        cs.constrainCircle(circle.getId(), compassLayout.getId(), 0, 0);
        cs.applyTo(cl);
    }

    /**
     * When our orientation changes, we recalculate friend positions on our compass.
     */
    public void observeOrientation() {
        orientationService.getOrientation().observe(this, orientation -> {
            float newOrientation = Calculation.getCompassRotation(orientation);
            if (Math.abs(compass.getRotation() - newOrientation) % 360 >= 1) {
                compass.setRotation(newOrientation);
                updateFriendLocations(users.getValue());
            }
        });
    }

    /**
     * @ensure remote DB is updated with lastMainLat and lastMainLong
     * @ensure friend locations on compass are updated according to new location with existing radius
     */
    public void observeLocation() {
        locationService.getLocation().observe(this, location -> {
            float distanceChange = Calculation.getDistance(lastMainLat, lastMainLong, location.first, location.second);
            if (distanceChange > 0.01) {
                lastMainLat = location.first;
                lastMainLong = location.second;

                User mainUser = viewModel.getUserNotLive(main_public_uid);
                mainUser.setLatitude(lastMainLat);
                mainUser.setLongitude(lastMainLong);

                userRepo.updateSynced(main_private_uid, mainUser);
                updateFriendLocations(users.getValue());
            }
        });
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, NewFriendActivity.class);
        startActivity(intent);
    }

    /**
     * @ensure friend locations on compass are updated according to new radius
     */
    public void onZoomIn(View view) {
        if (radiusIndex > 1) {
            radiusIndex--;
        }
        radius = radii[radiusIndex];
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Radius", radius);
        editor.putInt("Index", radiusIndex);
        editor.apply();

        updateCircles();
        updateFriendLocations(users.getValue());

        setClickable(findViewById(R.id.zoomOutButton));
        if (radiusIndex == 1) {
            setNotClickable(findViewById(R.id.zoomInButton));
        }
    }

    /**
     * @ensure friend locations on compass are updated according to new radius
     */
    public void onZoomOut(View view) {
        if (radiusIndex < 4) {
            radiusIndex++;
        }
        radius = radii[radiusIndex];
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Radius", radius);
        editor.putInt("Index", radiusIndex);
        editor.apply();

        updateCircles();
        updateFriendLocations(users.getValue());

        setClickable(findViewById(R.id.zoomInButton));
        if (radiusIndex == 4) {
            setNotClickable(findViewById(R.id.zoomOutButton));
        }
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackgroundColor(Color.parseColor("#FF6200EE"));
    }

    public void setNotClickable(Button button) {
        button.setClickable(false);
        button.setBackgroundColor(Color.GRAY);
    }

    /**
     * Creates a new compass marker for a single user.
     *
     * @param cl The layout in which we are going to place our inflated view
     * @param user The user who we are creating a view for
     * @return The LocationView which was created for user's compass marker
     */
    public LocationView addLocationView(ConstraintLayout cl, User user) {
        ConstraintLayout inflater = (ConstraintLayout) LayoutInflater.from(this)
                .inflate(R.layout.location, cl, false);
        LocationView userView = new LocationView(user, inflater);
        locationViews.put(user.public_code, userView);

        ConstraintSet set = new ConstraintSet();
        set.clone(inflater);
        set.connect(userView.statusView.getId(), ConstraintSet.TOP, userView.nameView.getId(), ConstraintSet.BOTTOM);
        set.connect(userView.timeView.getId(), ConstraintSet.TOP, userView.statusView.getId(), ConstraintSet.BOTTOM);
        set.applyTo(inflater);

        if (user.public_code.equals(main_public_uid)) {
            inflater.removeView(userView.nameView);
        }

        cl.addView(inflater);
        return userView;
    }
}