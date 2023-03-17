package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import database.Database;
import database.UserAPI;
import database.UserDao;
import database.UserRepository;
import model.User;
import view.LocationView;


/**
 * Tests for Story 6
 */
@RunWith(RobolectricTestRunner.class)
public class NewFriendTest {

    ActivityScenario<NewFriendActivity> scenario;
    UserRepository repo;

    UserAPI api;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
        api = new UserAPI();
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(NewFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    /**
     * Tests for an invalid friend code (Within the definition of our code)
     */
    @Test
    public void testAddInvalidUID() {
        scenario.onActivity(activity -> {
            EditText code = activity.findViewById(R.id.public_code);
            Button submit = activity.findViewById(R.id.submit_new_friend);
            code.setText("INVALID");
            submit.performClick();
            assertEquals("Invalid friend code.", code.getError());
        });
    }

    /**
     * Tests for an valid friend code on the remote repository
     */
    @Test
    public void testAddValidID() {
        scenario.onActivity(activity -> {
            EditText code = activity.findViewById(R.id.public_code);
            Button submit = activity.findViewById(R.id.submit_new_friend);
            code.setText("point-nemo");
            submit.performClick();
            assertEquals(repo.existsLocal("point-nemo"), true);
        });
    }

    /**
     * Given that I have my friends and family’s unique IDs
     * When I input these unique IDs into the app
     * Then it should show all their names, even when I leave the app.
     */
    @Test
    public void testAddMultipleFriends() {
        User user1 = new User("User 1", "pub_1", 0, 0);
        User user2 = new User("User 2", "pub_2", 0, 0);
        User user3 = new User("User 3", "pub_3", 0, 0);
        repo.upsertLocal(user1);
        repo.upsertLocal(user2);
        repo.upsertLocal(user3);
        ActivityScenario<CompassActivity> new_scenario = ActivityScenario.launch(CompassActivity.class);;
        new_scenario.onActivity(activity -> {
            Map<String, LocationView> location_views = activity.locationViews;
            assertEquals(location_views.size(), 3);
            assertNotNull(location_views.get("pub_1"));
            assertNotNull(location_views.get("pub_2"));
            assertNotNull(location_views.get("pub_3"));
        });
    }
}