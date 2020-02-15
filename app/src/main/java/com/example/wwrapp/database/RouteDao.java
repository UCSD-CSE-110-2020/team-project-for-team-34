package com.example.wwrapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.util.List;

@Dao
@TypeConverters({ListConverter.class, LocalDateTimeConverter.class})
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Route route);

    @Query("DELETE FROM route_table")
    void deleteAll();

    @Query("SELECT * from route_table ORDER BY routeName ASC")
    LiveData<List<Route>> getAlphabetizedRoutes();

    @Query("SELECT * FROM route_table WHERE routeName = :queryName LIMIT 1")
    Route findRouteByName(String queryName);

    @Query("SELECT * FROM route_table WHERE id = :routeId LIMIT 1")
    Route findRouteById(int routeId);

    @Query("UPDATE route_table SET steps = :updateSteps, miles = :updateMiles, " +
            "date = :updateDateTime WHERE id = :routeId")
    void updateLastWalkStats(int routeId, long updateSteps, double updateMiles, LocalDateTime updateDateTime);
}
