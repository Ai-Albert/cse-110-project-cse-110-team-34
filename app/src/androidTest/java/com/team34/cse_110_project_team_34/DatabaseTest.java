package com.team34.cse_110_project_team_34;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import java.util.List;

import utilities.Coordinate;
import utilities.Database;
import utilities.CoordinateDao;

public class DatabaseTest {

    private CoordinateDao coordinateDao;
    private Database database;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        coordinateDao = database.getCoordinateDao();
    }

    @After
    public void destroyDb() {
        database.close();
    }

    @Test
    public void emptyDatabaseTest() {
        List<Coordinate> coordinates = coordinateDao.getAll();
        assertEquals(0, coordinates.size());
    }

    @Test
    public void oneEntryTest() {
        Coordinate c = new Coordinate("c", 25.25, 50.5);
        coordinateDao.insert(c);

        List<Coordinate> coordinates = coordinateDao.getAll();
        assertEquals(1, coordinates.size());
        assertTrue(coordinates.get(0).equals(c));
    }

    @Test
    public void multipleEntryTest() {
        Coordinate c1 = new Coordinate("c1", 1.1, 2.2);
        coordinateDao.insert(c1);
        Coordinate c2 = new Coordinate("c2", 2.2, 3.3);
        coordinateDao.insert(c2);
        Coordinate c3 = new Coordinate("c3", 3.3, 4.4);
        coordinateDao.insert(c3);

        Coordinate dbCoordinate = coordinateDao.getByLabel("c1");
        assertTrue(dbCoordinate.equals(c1));
        dbCoordinate = coordinateDao.getByLabel("c2");
        assertTrue(dbCoordinate.equals(c2));
        dbCoordinate = coordinateDao.getByLabel("c3");
        assertTrue(dbCoordinate.equals(c3));
    }
}