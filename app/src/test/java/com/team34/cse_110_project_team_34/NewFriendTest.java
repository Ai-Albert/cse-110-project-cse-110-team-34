package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import database.Database;
import database.UserDao;
import database.UserRepository;


/**
 * Tests for Story 6
 */
@RunWith(RobolectricTestRunner.class)
public class NewFriendTest {

    ActivityScenario<NewFriendActivity> scenario;
    UserRepository repo;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
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
}