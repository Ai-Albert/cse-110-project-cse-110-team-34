package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import database.Database;
import database.UserAPI;
import database.UserDao;
import database.UserRepository;
import model.User;

/**
 * Tests for Story 5
 */

@RunWith(RobolectricTestRunner.class)
public class NewUserTest {

    ActivityScenario<NewUserActivity> scenario;
    UserRepository repo;

    UserAPI api;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
        api = new UserAPI();
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(NewUserActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    /**
     * Tests an invalid entered name (empty)
     */

    @Test
    public void testAddEmptyName() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);
            submit.performClick();
            assertEquals("User must have a name.", name.getError());
        });
    }

    /**
     * Tests an valid entered name and checks if it was entered into the local and remote database
     *
     * BDD: Given that I have never used the app,
     * When I open the app for the first time,
     * Then it should ask for a name and generate a UID for me.
     */
    @Test
    public void testAddNewUser() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);

            name.setText("Mary");
            submit.performClick();


            SharedPreferences preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

            assertTrue(preferences.contains("Public"));
            assertTrue(preferences.contains("Private"));

            String public_key = preferences.getString("Public", "");

            assertTrue(repo.existsLocal(public_key));
            User user = api.get(public_key);
            assertNotNull("User was not found on remote repository", user);
            assertEquals(user.getName(), "Mary");
        });
    }

    /**
     * Tests an valid entered name and checks if it was entered into the local and remote database
     *
     * BDD: Given that I have already have a UID
     * When I open the app
     * Then it should already have a name associated with my UID.
     */
    @Test
    public void testReturningUser() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);

            name.setText("Mary");
            submit.performClick();
        });

        ActivityScenario<MainActivity> new_scenario;
        new_scenario = ActivityScenario.launch(MainActivity.class);
        new_scenario.onActivity(activity -> {
            SharedPreferences preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

            assertTrue(preferences.contains("Public"));
            assertTrue(preferences.contains("Private"));
        });

    }
}
