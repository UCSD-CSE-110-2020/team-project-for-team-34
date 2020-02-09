package com.example.wwrapp.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RouteRepository {
        private RouteDao routeDao;
        private LiveData<List<Route>> allRoutes;

        RouteRepository(Application application) {
            RouteRoomDatabase db = RouteRoomDatabase.getDatabase(application);
            routeDao = db.routeDao();
            allRoutes = routeDao.getAlphabetizedRoutes();
        }

        // Room executes all queries on a separate thread.
        // Observed LiveData will notify the observer when the data has changed.
        LiveData<List<Route>> getAllRoutes() {
            return allRoutes;
        }

        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        void insert(Route route) {
            RouteRoomDatabase.databaseWriteExecutor.execute(() -> {
                routeDao.insert(route);
            });
        }

}
