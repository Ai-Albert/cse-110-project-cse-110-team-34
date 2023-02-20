package utilities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.Objects;

public class LocationService implements LocationListener {

    final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final AppCompatActivity activity;
    private static LocationService instance;
    private MutableLiveData<Pair<Double, Double>> locationValue;
    private final LocationManager locationManager;

    public static LocationService getInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new LocationService(activity);
        }
        return instance;
    }

    protected LocationService(AppCompatActivity activity) {
        this.locationValue = new MutableLiveData<>();
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        withLocationPermissions(this::registerLocationListener);
    }

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    private void registerLocationListener() {
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                LocationService.this);
    }

    private void withLocationPermissions(Runnable action) {
        if (Arrays.stream(REQUIRED_PERMISSIONS).allMatch(perm -> activity.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) {
            action.run();
        } else {
            ActivityResultLauncher<String[]> launcher = activity.registerForActivityResult(new RequestMultiplePermissions(), grants -> {
                if (grants.values().stream().noneMatch(isGranted -> isGranted)) {
                    throw new IllegalStateException("App needs you to grant at least one location permission!");
                }
                action.run();
            });
            launcher.launch(REQUIRED_PERMISSIONS);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.locationValue.postValue(new Pair<>(location.getLatitude(), location.getLongitude()));
    }

    public void unregisterLocationListener() {
        locationManager.removeUpdates(this);
    }

    public LiveData<Pair<Double, Double>> getLocation() {
        return this.locationValue;
    }

    public void setMockOrientationData(MutableLiveData<Pair<Double, Double>> mockData) {
        unregisterLocationListener();
        this.locationValue = mockData;
    }
}
