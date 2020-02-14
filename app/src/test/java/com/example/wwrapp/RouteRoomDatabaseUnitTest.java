package com.example.wwrapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wwrapp.database.Route;
import com.example.wwrapp.database.RouteDao;
import com.example.wwrapp.database.RouteRoomDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit tests for the Route database, implemented using the Room Persistence Library
 */
@RunWith(AndroidJUnit4.class)
public class RouteRoomDatabaseUnitTest {
    private static final double ACCEPTABLE_ERROR = 0.001;

    private RouteDao routeDao;
    private RouteRoomDatabase routeRoomDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        routeRoomDatabase = Room.inMemoryDatabaseBuilder(context, RouteRoomDatabase.class)
                .allowMainThreadQueries() // We need this line to run the tests on the main thread
                .build();
        routeDao = routeRoomDatabase.routeDao();
    }

    @After
    public void closeDatabase() throws IOException {
        routeRoomDatabase.close();
    }

    @Test
    public void createRouteAndRead() throws Exception {
        // Set the data for the Route object
        String routeName = "Test Route Name 1";
        String startingPoint = "Test Starting Point 1";
        int year = 2020;
        int month = 3;
        int dayOfMonth = 15;
        int hour = 23;
        int minutes = 30;
        int seconds = 45;
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minutes, seconds);
        String duration = "0 hours, 5 minutes, 20 seconds";
        long steps = 1000;
        int feet = 5;
        int inches = 3;
        StepsAndMilesConverter stepsAndMilesConverter = new StepsAndMilesConverter(feet, inches);
        double miles = stepsAndMilesConverter.getNumMiles(steps);
        String[] tags = {"out-and-back", "hilly", "even", "easy"};
        List<String> tagList = Arrays.asList(tags);
        boolean isFavorite = true;
        String notes = "Test Route Notes 1";

        // Create and insert the route
        Route routeToInsert = new Route(routeName, startingPoint, localDateTime, duration, steps, miles, tagList, isFavorite, notes);
        routeDao.insert(routeToInsert);

        // Read the route back
        Route routeQueried = routeDao.findRouteByName(routeName);

        // Check for correctness
        assertEquals(routeToInsert.getRouteName(), routeQueried.getRouteName());
        assertEquals(routeToInsert.getStartingPoint(), routeQueried.getStartingPoint());
        assertEquals(routeToInsert.getDate(), routeQueried.getDate());
        assertEquals(routeToInsert.getDuration(), routeQueried.getDuration());
        assertEquals(routeToInsert.getSteps(), routeQueried.getSteps());
        assertEquals(routeToInsert.getDuration(), routeQueried.getDuration());
        assertEquals(routeToInsert.getMiles(), routeQueried.getMiles(), ACCEPTABLE_ERROR);
        assertEquals(routeToInsert.getDuration(), routeQueried.getDuration());
        assertEquals(routeToInsert.getTags(), routeQueried.getTags());
        assertEquals(routeToInsert.isFavorite(), routeQueried.isFavorite());
        assertEquals(routeToInsert.getNotes(), routeQueried.getNotes());
    }

    @Test
    public void updateRouteAndRead() throws Exception {
        int feet = 5;
        int inches = 3;
        StepsAndMilesConverter stepsAndMilesConverter = new StepsAndMilesConverter(feet, inches);

        long stepsToUpdate = 1000;
        double milesToUpdate = stepsAndMilesConverter.getNumMiles(stepsToUpdate);

        // Create 2 Route objects to check that the query updates only 1 of them
        String routeName1 = "Test Route Name 1";
        String durationToUpdate = "0 hours, 5 minutes, 20 seconds";
        Route routeToUpdate = new Route(routeName1, null, null,
                durationToUpdate, stepsToUpdate, milesToUpdate, null, false, null);

        String routeName2 = "Test Route Name 2";
        String durationToStayTheSame = "0 hours, 2 minutes, 10 seconds";
        long stepsToStayTheSame = 2435;
        double milesToStayTheSame = stepsAndMilesConverter.getNumMiles(stepsToStayTheSame);
        Route routeToStayTheSame = new Route(routeName2, null, null,
                durationToStayTheSame, stepsToStayTheSame, milesToStayTheSame, null, false, null);

        // Insert the routes
        routeDao.insert(routeToUpdate);
        routeDao.insert(routeToStayTheSame);

        // Update 1 of the routes
        long newSteps = 5000;
        double newMiles = stepsAndMilesConverter.getNumMiles(newSteps);
        String newDuration = "1 hour, 2 minutes, 3 seconds";

        // First, get the ID of the route to update
        Route queriedRouteToUpdate = routeDao.findRouteByName(routeToUpdate.getRouteName());
        routeDao.updateLastWalkStats(queriedRouteToUpdate.getId(), newSteps, newMiles, newDuration);
        // Re-query the route to get the updates
        queriedRouteToUpdate = routeDao.findRouteById(queriedRouteToUpdate.getId());

        Route queriedRouteToStayTheSame = routeDao.findRouteByName(routeToStayTheSame.getRouteName());

        // Check that the the target route was updated correctly
        assertEquals(newSteps, queriedRouteToUpdate.getSteps());
        assertEquals(newMiles, queriedRouteToUpdate.getMiles(), ACCEPTABLE_ERROR);
        assertEquals(newDuration, queriedRouteToUpdate.getDuration());

        // Ensure that no other fields were updated - simple test, not exhaustive
        assertEquals(routeName1, queriedRouteToUpdate.getRouteName());

        // Check that the other route in the database wasn't updated
        assertEquals(routeToStayTheSame, queriedRouteToStayTheSame);
    }
}
