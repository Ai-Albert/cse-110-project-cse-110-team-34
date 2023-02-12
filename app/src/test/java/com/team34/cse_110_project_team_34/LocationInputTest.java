package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.room.Room;
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

    @Rule
    public ActivityScenarioRule context = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testStudentNameIsDisplayed() {
        context.getScenario().onActivity(activity -> {
            EditText name = (EditText) activity.findViewById(R.id.Coordinates);
            EditText coords = (EditText) activity.findViewById(R.id.LocationName);
            Button submit = (Button) activity.findViewById(R.id.Submit);
            TextView remaining_display = (TextView) activity.findViewById(R.id.remaining);
            name.setText("Location 1");
            coords.setText("ab455.26742, 256.21312");
            submit.performClick();

            assertEquals(coords.getError(), null);
        });

    }

}
