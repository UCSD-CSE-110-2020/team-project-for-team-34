package com.example.wwrapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.database.Route;
import com.example.wwrapp.database.RouteViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoutesActivity extends AppCompatActivity {

    private static final String TAG = "RoutesActivity";
    private RouteViewModel mRouteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "onCreate: started");
        RecyclerView recyclerView = findViewById(R.id.recycler_view_route);
        final RouteListAdapter adapter = new RouteListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRouteViewModel = new ViewModelProvider(this).get(RouteViewModel.class);
        mRouteViewModel.getAllRoutes().observe(this, new Observer<List<Route>>() {
            @Override
            public void onChanged(@Nullable final List<Route> routes) {
                // Update the cached copy of the words in the adapter.
                adapter.setRoutes(routes);
            }
        });


        Intent intent = getIntent();
        String callerID = intent.getStringExtra(EnterWalkInformationActivity.CALLER_ID_KEY);
        Log.i(TAG, "callerID is: " + callerID);
        // If the Routes activity was launched by the EnterWalkInformation activity, then
        // update the routes database.
        switch (callerID) {
            case EnterWalkInformationActivity.CALLER_ID:
                String routeName = intent.getStringExtra(EnterWalkInformationActivity.ROUTE_NAME_KEY);
                String startingPoint = intent.getStringExtra(EnterWalkInformationActivity.ROUTE_STARTING_POINT_KEY);
                String duration = intent.getStringExtra(EnterWalkInformationActivity.ROUTE_DURATION_KEY);
                LocalDateTime date = (LocalDateTime) (intent.getSerializableExtra(EnterWalkInformationActivity.ROUTE_DATE_KEY));
                int steps = intent.getIntExtra(EnterWalkInformationActivity.ROUTE_STEPS_KEY, 0);
                double miles = intent.getDoubleExtra(EnterWalkInformationActivity.ROUTE_MILES_KEY, 0.0);

                if (startingPoint == null) {
                    startingPoint = "No starting point";
                }

                if (date == null) {
                    Log.e(TAG, "LocalDateTime is null");
                    date = LocalDateTime.now();
                }

                Log.d(TAG, "Route steps are: " + steps);
                Log.d(TAG, "Route miles are: " + miles);
                Log.d(TAG, "Route duration is: " + duration);


                Route route = new Route(routeName, startingPoint, date, duration, steps, miles);
                Log.i(TAG, "Successfully created Route object");
                // Save the newest walk
                SharedPreferences sharedPreferences =
                        getSharedPreferences(HomeScreenActivity.LAST_WALK_SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(HomeScreenActivity.LAST_WALK_STEPS_KEY, steps);
                editor.putFloat(HomeScreenActivity.LAST_WALK_MILES_KEY, (float) miles);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                String formattedDate = date.format(formatter);
                editor.putString(HomeScreenActivity.LAST_WALK_TIME_KEY, formattedDate);
                editor.apply();
                mRouteViewModel.insert(route);


                break;
            case HomeScreenActivity.CALLER_ID:
                break;
        }

    }


}
