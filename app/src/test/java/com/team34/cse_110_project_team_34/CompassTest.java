package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import utilities.Calculation;
import utilities.Coordinate;
import utilities.CoordinateDao;
import utilities.Database;
import utilities.LocationService;
import utilities.OrientationService;


@RunWith(RobolectricTestRunner.class)
public class CompassTest {
    ActivityScenario<CompassActivity> scenario;
    private CoordinateDao coordinateDao;
    private Context context;

    @Before
    public void preTest() {
        context = ApplicationProvider.getApplicationContext();
        coordinateDao = Database.getInstance(context).getCoordinateDao();
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testLocationLabels() {

        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.onActivity(activity -> {
            Coordinate c1 = new Coordinate("c1", 0, 0);
            coordinateDao.insert(c1);
            Coordinate c2 = new Coordinate("c2", 2.2, 3.3);
            coordinateDao.insert(c2);
            Coordinate c3 = new Coordinate("c3", 3.3, 4.4);
            coordinateDao.insert(c3);
            activity.updatePerimeter();

            TextView loc_1 = activity.findViewById(R.id.location_1);
            TextView loc_2 = activity.findViewById(R.id.location_2);
            TextView loc_3 = activity.findViewById(R.id.location_3);

            assertEquals(loc_1.getText(), "c1");
            assertEquals(loc_2.getText(), "c2");
            assertEquals(loc_3.getText(), "c3");

        });
    }

    @Test
    public void testLocationPosition() {
        float testOrientation = 0;
        Double testLocationLat = 100.0;
        Double testLocationLong = 100.0;
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        Coordinate c1 = new Coordinate("c1", 0, 0);
        coordinateDao.insert(c1);

        scenario.onActivity(activity -> {
            TextView loc_1 = activity.findViewById(R.id.location_1);

            OrientationService orientationService = OrientationService.getInstance(activity);
            MutableLiveData<Float> mockOrientation = new MutableLiveData<Float>();

            mockOrientation.setValue(testOrientation);
            orientationService.setMockOrientationSource(mockOrientation);
            activity.observeOrientation();

            LocationService locationService = LocationService.getInstance(activity);
            MutableLiveData<Pair<Double, Double>> mockLocation = new MutableLiveData<>();
            Pair<Double, Double> pair = new Pair<>(testLocationLat, testLocationLong);
            mockLocation.setValue(pair);
            locationService.setMockOrientationData(mockLocation);
            activity.observeLocation();

            activity.updatePerimeter();

            float azimuth = Calculation.getAngle(
                    Math.toRadians(testLocationLat),
                    Math.toRadians(testLocationLong),
                    Math.toRadians(c1.getLatitude()),
                    Math.toRadians(c1.getLongitude())
            );
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) loc_1.getLayoutParams();
            assertEquals(layoutParams.circleAngle, azimuth, 0);

        });
    }
}
