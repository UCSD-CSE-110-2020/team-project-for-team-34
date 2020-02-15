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

import java.util.List;

public class RoutesActivity extends AppCompatActivity implements RouteListAdapter.OnRouteListener{

    private static final String TAG = "RoutesActivity";
    public static final String ROUTE_KEY = "RouteKey";
    private RouteViewModel mRouteViewModel;
    RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "onCreate: started");

        Route testRoute = new Route("route","staring",null,"",10,10,null,true,"");

        initRecyclerView();

        mRouteViewModel = new ViewModelProvider(this).get(RouteViewModel.class);
        mRouteViewModel.insert(testRoute);
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
                Intent incomingIntent = getIntent();
                Route route = (Route) (incomingIntent.getSerializableExtra(EnterWalkInformationActivity.ROUTE_KEY));

                if (route.getDate() == null) {
                    Log.e(TAG, "LocalDateTime is null");
                }

                Log.d(TAG, route.toString());

                // Save the newest walk
                SharedPreferences sharedPreferences =
                        getSharedPreferences(HomeScreenActivity.LAST_WALK_SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(HomeScreenActivity.LAST_WALK_STEPS_KEY, route.getSteps());
                editor.putFloat(HomeScreenActivity.LAST_WALK_MILES_KEY, (float) route.getMiles());
                editor.putString(HomeScreenActivity.LAST_WALK_TIME_KEY, route.getDuration());
                editor.apply();
                mRouteViewModel.insert(route);
                break;
            case HomeScreenActivity.CALLER_ID:
                break;
        }

    }



    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.recycler_view_route);
        adapter = new RouteListAdapter(this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRouteClick(int position,List<Route> routes) {
        Intent intent = new Intent(this, RouteDetailActivity.class);
        intent.putExtra(ROUTE_KEY,routes.get(position));
        startActivity(intent);
    }
}
