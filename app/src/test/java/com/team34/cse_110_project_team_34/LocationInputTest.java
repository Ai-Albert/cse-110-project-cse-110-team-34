package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import utilities.CoordinateDao;
import utilities.Database;

@RunWith(RobolectricTestRunner.class)
public class LocationInputTest {

    ActivityScenario<AddActivity> scenario;
    CoordinateDao dao;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        dao = Database.getInstance(context).getCoordinateDao();
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(AddActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testAddNoLocations() {
        scenario.onActivity(activity -> {
            TextView remaining_display = activity.findViewById(R.id.remaining);
            assertEquals("You have 3 locations left.", remaining_display.getText().toString());
        });
    }

    @Test
    public void testAddSingleLocation() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.LocationName);
            EditText coords = activity.findViewById(R.id.Coordinates);
            Button submit = activity.findViewById(R.id.Submit);
            TextView remaining_display = activity.findViewById(R.id.remaining);

            name.setText("Location 1");
            coords.setText("455, 256");
            submit.performClick();

            assertEquals("You have 2 locations left.", remaining_display.getText().toString());
        });
    }

    @Test
    public void testAddAllLocations() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.LocationName);
            EditText coords = activity.findViewById(R.id.Coordinates);
            Button submit = activity.findViewById(R.id.Submit);
            TextView remaining_display = activity.findViewById(R.id.remaining);

            name.setText("Location 1");
            coords.setText("455, 256");
            submit.performClick();
            assertEquals("You have 2 locations left.", remaining_display.getText().toString());

            name.setText("Location 2");
            coords.setText("100, 200");
            submit.performClick();
            assertEquals("You have 1 locations left.", remaining_display.getText().toString());

            name.setText("Location 3");
            coords.setText("200, 300");
            submit.performClick();
            assertEquals("You have 0 locations left.", remaining_display.getText().toString());
        });
    }

    @Test
    public void testAddTooManyLocations() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.LocationName);
            EditText coords = activity.findViewById(R.id.Coordinates);
            Button submit = activity.findViewById(R.id.Submit);
            TextView remaining_display = activity.findViewById(R.id.remaining);

            name.setText("Location 1");
            coords.setText("455, 256");
            submit.performClick();
            assertEquals("You have 2 locations left.", remaining_display.getText().toString());

            name.setText("Location 2");
            coords.setText("100, 200");
            submit.performClick();
            assertEquals("You have 1 locations left.", remaining_display.getText().toString());

            name.setText("Location 3");
            coords.setText("200, 300");
            submit.performClick();
            assertEquals("You have 0 locations left.", remaining_display.getText().toString());

            name.setText("Location 4");
            coords.setText("500, 500");
            submit.performClick();
            assertEquals(3, dao.getAll().size());
            assertEquals("You have 0 locations left.", remaining_display.getText().toString());
        });
    }

    @Test
    public void testSkip() {
        scenario.onActivity(activity -> {
            Button skip = activity.findViewById(R.id.skip);
            TextView remaining_display = activity.findViewById(R.id.remaining);

            skip.performClick();
            assertEquals(0, Database.getInstance(activity).getCoordinateDao().getAll().size());
            assertEquals("You have 3 locations left.", remaining_display.getText().toString());
        });
    }
}
