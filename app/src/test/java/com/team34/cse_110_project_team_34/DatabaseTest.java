package com.team34.cse_110_project_team_34;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.List;

import database.Database;
import database.UserDao;
import model.User;


@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private UserDao userDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        userDao = Database.getInstance(context).getUserDao();
        Database.getInstance(context).clearAllTables();
    }

    @Test
    public void emptyDatabaseTest() {
        List<User> users = userDao.getAllList();
        assertEquals(0, users.size());
    }

    @Test
    public void oneEntryTest() {
        User u = new User("name","public_code", 0,0);
        userDao.upsert(u);

        List<User> users = userDao.getAllList();
        assertEquals(1, users.size());
        assertTrue(users.get(0).equals(u));
    }

    @Test
    public void multipleEntryTest() {
        User u1 = new User("u1", "public1", 0,0);
        userDao.upsert(u1);
        User u2 = new User("u2", "public2", 0,0);
        userDao.upsert(u2);
        User u3 = new User("u3", "public3", 0,0);
        userDao.upsert(u3);


        User db1 = userDao.getNotLive("public1");
        assertTrue(db1.equals(u1));
        User db2 = userDao.getNotLive("public2");
        assertTrue(db2.equals(u2));
        User db3 = userDao.getNotLive("public3");
        assertTrue(db3.equals(u3));
    }
}