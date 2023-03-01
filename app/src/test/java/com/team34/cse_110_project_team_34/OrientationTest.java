//package com.team34.cse_110_project_team_34;
//
//import static org.junit.Assert.assertEquals;
//
//import android.content.Context;
//import android.content.pm.ActivityInfo;
//import android.media.Image;
//import android.provider.SyncStateContract;
//import android.widget.ImageView;
//
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.MutableLiveData;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//
//import com.ibm.icu.impl.units.UnitsData;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//
//import utilities.OrientationService;
//
//
//@RunWith(RobolectricTestRunner.class)
//public class OrientationTest {
//    ActivityScenario<CompassActivity> scenario;
//
//    @Before
//    public void preTest() {
//        scenario = ActivityScenario.launch(CompassActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//    }
//
//    // OrientationService's azimuth is in radians
//    // Compass's rotation is in degrees
//    @Test
//    public void testOrientation() {
//        float testValue = (float) Math.PI;
//        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
//
//        scenario.onActivity(activity -> {
//            OrientationService orientationService = OrientationService.getInstance(activity);
//            MutableLiveData<Float> mockOrientation = new MutableLiveData<Float>();
//
//            mockOrientation.setValue(testValue);
//            orientationService.setMockOrientationSource(mockOrientation);
//            activity.observeOrientation();
//
//            ImageView compass = activity.findViewById(R.id.compass);
//            assertEquals(Math.toDegrees(testValue), compass.getRotation(), 1);
//        });
//    }
//}
