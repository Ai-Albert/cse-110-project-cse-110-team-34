package com.team34.cse_110_project_team_34;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import database.Database;
import database.UserRepository;
import model.User;
import utilities.Calculation;
import utilities.LocationService;
import utilities.OrientationService;
import view.LocationAdapter;
import viewModel.LocationViewModel;

public class CompassActivity extends AppCompatActivity {

    private UserRepository userRepo;
    private SharedPreferences preferences;
    private OrientationService orientationService;
    private LocationService locationService;

    private LiveData<User> mainUser;

    private double lastMainLat;
    private double lastMainLong;

    @VisibleForTesting
    public double radius; // Miles

    private ImageView compass;

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public RecyclerView recyclerView;

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
        preferences = getPreferences(MODE_PRIVATE);
        Log.d("create", preferences.getString("Private", ""));
        mainUser = userRepo.getLocal(preferences.getString("Public", ""));

        orientationService = OrientationService.getInstance(this);
        locationService = LocationService.getInstance(this);

        compass = findViewById(R.id.compass);
        radius = 20;

        lastMainLat = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().first : 0;
        lastMainLong = locationService.getLocation().getValue() != null ? locationService.getLocation().getValue().second : 0;

        observeLocation();
        observeOrientation();
        LocationViewModel viewModel = setupViewModel();
        LocationAdapter adapter = setupAdapter(viewModel);

        setupViews(adapter);
    }

    private LocationViewModel setupViewModel() {
        return new ViewModelProvider(this).get(LocationViewModel.class);
    }

    @NonNull
    private LocationAdapter setupAdapter(LocationViewModel viewModel) {
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        String public_uid = preferences.getString("Public", "null");

        LocationAdapter adapter = new LocationAdapter(public_uid);
        adapter.setHasStableIds(true);
        viewModel.getUsers().observe(this, adapter::setUsers);
        return adapter;
    }

    private void setupViews(LocationAdapter adapter) {
        setupRecycler(adapter);
    }

    @SuppressLint("RestrictedApi")
    private void setupRecycler(LocationAdapter adapter) {
        // We store the recycler view in a field _only_ because we will want to access it in tests.
        recyclerView = findViewById(R.id.recycler_main);
        // TODO: Make a custom layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void updateFriendLocations() {
        // TODO: use last fetched friend users lat/long to calculate radius and angle for compass placement
        //Log.d("update", preferences.getString("Private", ""));
//        Log.d("Update", mainUser.getValue().toPatchJSON("aaa"));
//        userRepo.upsertSynced(preferences.getString("Private", ""), mainUser.getValue());
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
            // TODO: update main user's location in remote DB
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

    public void addTextView(View view) {
        RelativeLayout rl = new RelativeLayout(this);
        TextView newText = new TextView(this);
        newText.setText("dynamically added a view wow!");
        rl.addView(newText);

    }
}