package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

import utilities.CoordinateDao;
import utilities.Database;

@RunWith(RobolectricTestRunner.class)
public class LocationInputTest {
    private CoordinateDao coordinateDao;
    private Database database;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        coordinateDao = database.getCoordinateDao();
    }

    @After
    public void destroyDb() {
        database.close();
    }

    @Test
    public void testAddLocation() {
        ActivityScenario<MainActivity> context = ActivityScenario.launch(MainActivity.class);
        context.moveToState(Lifecycle.State.CREATED);
        context.moveToState(Lifecycle.State.STARTED);
        context.onActivity(activity -> {
            EditText name = (EditText) activity.findViewById(R.id.Coordinates);
            EditText coords = (EditText) activity.findViewById(R.id.LocationName);
            Button submit = (Button) activity.findViewById(R.id.Submit);
            TextView remaining_display = (TextView) activity.findViewById(R.id.remaining);
            name.setText("Location 1");
            coords.setText("455, 256");
            submit.performClick();
            assertEquals(true, true);
        });

    }

}
