package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ZoomTest {
    ActivityScenario<CompassActivity> scenario;
    private Context context;

    @Before
    public void preTest() {
        context = ApplicationProvider.getApplicationContext();

        scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testZoomIn() {
        scenario.onActivity(activity -> {
            assertEquals(20, activity.radius, 1);
            Button zoomIn = activity.findViewById(R.id.zoomInButton);
            zoomIn.performClick();
            assertEquals(13.33, activity.radius, 1);
        });
    }

    @Test
    public void testZoomOut() {
        scenario.onActivity(activity -> {
            assertEquals(20, activity.radius, 1);
            Button zoomOut = activity.findViewById(R.id.zoomOutButton);
            zoomOut.performClick();
            assertEquals(30, activity.radius, 1);
        });
    }
}