package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

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

import model.Database;
import model.UserDao;
import model.UserRepository;

@RunWith(RobolectricTestRunner.class)
public class NewUserTest {

    ActivityScenario<NewUserActivity> scenario;
    UserRepository repo;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        UserDao dao = Database.getInstance(context).getUserDao();
        repo = new UserRepository(dao);
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(NewUserActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testAddEmptyName() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);
            submit.performClick();
            assertEquals("User must have a name.", name.getError());
        });
    }

    @Test
    public void testAddNewUser() {
        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name);
            Button submit = activity.findViewById(R.id.submit_new_user);

            name.setText("Mary");
            submit.performClick();

            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            assertEquals(preferences.contains("Public"), true);
            assertEquals(preferences.contains("Private"), true);

            assertEquals(repo.existsLocal(preferences.getString("Public", "")), true);
        });
    }
}
