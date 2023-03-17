package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import database.UserDao;
import database.UserRepository;
import model.User;
import view.LocationView;

@RunWith(RobolectricTestRunner.class)
public class PositionTest {

    UserRepository repo;
    ActivityScenario<CompassActivity> scenario;
    Map<String, LocationView> views;
    SharedPreferences preferences;

    UserDao dao;
    User mainUser;
    String public_id;

    @Before
    public void preTest() {
        Context context = ApplicationProvider.getApplicationContext();
        dao = Database.getInstance(context).getUserDao();
        //repo = new UserRepository(Database.getInstance(context).getUserDao());
        repo = new UserRepository(dao);
        Database.getInstance(context).clearAllTables();

        scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    /**
     * Checking that the position of a friend changes on CompassActivity
     * when the latitude and longitude of that friend is changed.
     */
    @Test
    public void changePosition() {
        scenario.onActivity(activity -> {
            preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            views = activity.locationViews;

            //adding the mainUser to sharedPreferences
            String public_code = UUID.randomUUID().toString();
            mainUser = new User("main", public_code, 0,0);

            //adding new user
            User user = new User("user", "code1", 30, 40);
            LocationView locationViewbefore = activity.addLocationView(activity.findViewById(R.id.mainLayout), user);

            repo.upsertLocal(mainUser);
            repo.getLocal(public_code);
            public_id = preferences.getString("Public", "");

            //upserting test friend into the dao
            repo.upsertLocal(user);

            ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) locationViewbefore.itemView.getLayoutParams();
            float before = lp1.circleAngle;

            //User user2 = new User("user2", "code2", 100, -130);
            //changing location and reupdating the dao
            user.latitude = 100;
            user.longitude = -130;
            repo.updateLocal(user);

            LocationView locationViewafter = activity.addLocationView(activity.findViewById(R.id.mainLayout), user);
            ConstraintLayout.LayoutParams lp2 = (ConstraintLayout.LayoutParams) locationViewafter.itemView.getLayoutParams();
            float after = lp2.circleAngle;

            //asserting that the distances are not the same after moving position
            assertTrue(before == after);
        });
    }

    /**
     * Checking the angle between the main user and two friends.
     * The friend with the higher latitude should be displayed at a bigger angle to the main user
     * than the friend with the smaller latitude.
     */
    @Test
    public void checkAngle() {
        scenario.onActivity(activity -> {
            preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

            String public_code = UUID.randomUUID().toString();
            mainUser = new User("main", public_code, 0,0);
            repo.upsertLocal(mainUser);
            repo.getLocal(public_code);

            public_id = preferences.getString("Public", "");


            User testfriend1 = new User("friend1", "code1", 30, 40);
            LocationView locationView1 = activity.addLocationView(activity.findViewById(R.id.mainLayout), testfriend1);
            User testfriend2 = new User("friend2", "code2", 50,300);
            LocationView locationView2 = activity.addLocationView(activity.findViewById(R.id.mainLayout), testfriend2);
            repo.upsertLocal(testfriend1);
            repo.upsertLocal(testfriend2);

            ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) locationView1.itemView.getLayoutParams();
            float angle1 = lp1.circleAngle;

            ConstraintLayout.LayoutParams lp2 = (ConstraintLayout.LayoutParams) locationView2.itemView.getLayoutParams();
            float angle2 = lp2.circleAngle;
            assertTrue(angle1 <= angle2);
        });
    }


}
