package com.team34.cse_110_project_team_34;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import utilities.Calculation;


@RunWith(AndroidJUnit4.class)
public class CalculationTest {
    double lat1 = 0;
    double long1 = 0;
    double lat2 = 40;
    double long2 = 30;

    @Test
    public void checkAngle() {
        assertEquals(Math.toDegrees(Math.asin(3/(float) 5)), Calculation.getAngle(lat1,long1,lat2,long2), 1);
    }

    @Test
    public void checkDistance() {
        assertEquals(69*Math.sqrt((lat2-lat1)*(lat2-lat1) + (long2-long1)*(long2-long1)), Calculation.getDistance(lat1,long1,lat2,long2),1);
    }
}
