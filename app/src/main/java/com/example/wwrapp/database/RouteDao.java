package com.example.wwrapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RouteDao {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insert(Route route);

        @Query("DELETE FROM route_table")
        void deleteAll();

        @Query("SELECT * from route_table ORDER BY routeName ASC")
        LiveData<List<Route>> getAlphabetizedRoutes();
}
