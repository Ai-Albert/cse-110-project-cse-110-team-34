//package com.team34.cse_110_project_team_34;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//import android.content.Context;
//
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import java.util.List;
//
//import utilities.User;
//import utilities.Database;
//import utilities.UserDao;
//
//@RunWith(AndroidJUnit4.class)
//public class DatabaseTest {
//
//    private UserDao userDao;
//
//    @Before
//    public void createDb() {
//        Context context = ApplicationProvider.getApplicationContext();
//        userDao = Database.getInstance(context).getUserDao();
//        Database.getInstance(context).clearAllTables();
//    }
//
//    @Test
//    public void emptyDatabaseTest() {
//        List<User> users = userDao.getAll();
//        assertEquals(0, users.size());
//    }
//
//    @Test
//    public void oneEntryTest() {
//        User c = new User("c", 25.25, 50.5);
//        userDao.insert(c);
//
//        List<User> users = userDao.getAll();
//        assertEquals(1, users.size());
//        assertTrue(users.get(0).equals(c));
//    }
//
//    @Test
//    public void multipleEntryTest() {
//        User c1 = new User("c1", 1.1, 2.2);
//        userDao.insert(c1);
//        User c2 = new User("c2", 2.2, 3.3);
//        userDao.insert(c2);
//        User c3 = new User("c3", 3.3, 4.4);
//        userDao.insert(c3);
//
//        User dbUser = userDao.getByLabel("c1");
//        assertTrue(dbUser.equals(c1));
//        dbUser = userDao.getByLabel("c2");
//        assertTrue(dbUser.equals(c2));
//        dbUser = userDao.getByLabel("c3");
//        assertTrue(dbUser.equals(c3));
//    }
//}