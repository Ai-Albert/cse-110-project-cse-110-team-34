package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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

    final String public_code = UUID.randomUUID().toString();
    final String private_code = UUID.randomUUID().toString();

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
        api = new UserAPI();
        Database.getInstance(context).clearAllTables();


        User user = new User("Mary", public_code, 0, 0);
        api.put(private_code, user);
        repo.getSynced(public_code);

        scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    /**
     * Tests an invalid entered name (empty)
     */
    @Test
    public void testIndicator() {
        scenario.onActivity(activity -> {
            ConstraintLayout layout = activity.findViewById(R.id.mainLayout);
            Map<String, LocationView> views = activity.getLocationsViews();
            LocationView view = views.get(public_code);
            assertNotNull("Invalid View", view);
            assertEquals(view.statusView.getTag(), R.drawable.green_indicator);
            try {
                Thread.sleep(62000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            assertEquals(view.statusView.getTag(), R.drawable.red_indicator);
        });
    }

    /**
     * Tests an valid entered name and checks if it was entered into the local database
     */
    @Test
    public void testTimer() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);

            name.setText("Mary");
            submit.performClick();

            SharedPreferences preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            assertEquals(preferences.contains("Public"), true);
            assertEquals(preferences.contains("Private"), true);

            assertEquals(repo.existsLocal(preferences.getString("Public", "")), true);
        });
    }
}
