package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import database.Database;
import database.UserAPI;
import database.UserDao;
import database.UserRepository;
import model.User;
import utilities.Calculation;
import view.LocationView;

/**
 * Tests for Story 8
 */
@RunWith(RobolectricTestRunner.class)
public class LocationTimeTest {

    ActivityScenario<CompassActivity> scenario;
    UserRepository repo;

    UserAPI api;

    User user;

    final String public_code = UUID.randomUUID().toString();
    final String private_code = UUID.randomUUID().toString();

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
        api = new UserAPI();
        Database.getInstance(context).clearAllTables();


        user = new User("Mary", public_code, 0, 0);
        api.put(private_code, user);
        repo.getSynced(public_code);

        scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    /**
     * Tests the correctness of the indicator
     */
    @Test
    public void testIndicator() {
        scenario.onActivity(activity -> {
            ConstraintLayout layout = activity.findViewById(R.id.mainLayout);
            Map<String, LocationView> views = activity.getLocationsViews();
            LocationView view = views.get(public_code);
            assertNotNull("Invalid View", view);
            assertEquals(view.statusView.getTag(), R.drawable.green_indicator);
            user.setLastUpdated(user.getLastUpdated() - 100);
            repo.updateLocal(user);
            assertEquals(view.statusView.getTag(), R.drawable.red_indicator);
        });
    }

    /**
     * Tests the correctness of the timer.
     */
    @Test
    public void testTimer() {
        scenario.onActivity(activity -> {
            ConstraintLayout layout = activity.findViewById(R.id.mainLayout);
            Map<String, LocationView> views = activity.getLocationsViews();
            LocationView view = views.get(public_code);
            assertNotNull("Invalid View", view);
            assertEquals(view.timeView.getVisibility(), View.INVISIBLE);
            user.setLastUpdated(user.getLastUpdated() - 300);
            repo.updateLocal(user);
            String expected_text = "6m";
            assertEquals(view.timeView.getText().toString(), expected_text);
        });
    }

    
}
