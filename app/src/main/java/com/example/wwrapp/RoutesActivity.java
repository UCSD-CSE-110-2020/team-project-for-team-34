package com.example.wwrapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.database.Route;
import com.example.wwrapp.database.RouteViewModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoutesActivity extends AppCompatActivity implements RouteListAdapter.OnRouteListener {

    private static final String TAG = "RoutesActivity";
    private static final int START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE = 1;
    private static final int START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE = 2;
    private RouteViewModel mRouteViewModel;
    private Button mAddNewRouteButton;

    RouteListAdapter adapter;
    private static boolean sIsTest = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "onCreate: started");

//        initRecyclerView();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_route);
        final RouteListAdapter routeListAdapter = new RouteListAdapter(this, this);
        recyclerView.setAdapter(routeListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRouteViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

//        // Testing code block
//        if (sIsTest) {
//            generateFakeRoute();
//        }

        mRouteViewModel.getAllRoutes().observe(this, new Observer<List<Route>>() {
            @Override
            public void onChanged(@Nullable final List<Route> routes) {
                // Update the cached copy of the routes in the adapter.
                routeListAdapter.setRoutes(routes);
            }
        });
        Intent intent = getIntent();
        String callerId = intent.getStringExtra(WWRConstants.EXTRA_CALLER_ID_KEY);
        Log.i(TAG, "callerID is: " + callerId);

        // If the Routes activity was launched by the EnterWalkInformation activity, then
        // update the routes database.
        switch (callerId) {
            case WWRConstants.EXTRA_ENTER_WALK_INFORMATION_ACTIVITY_CALLER_ID:
                Intent incomingIntent = getIntent();
                Route route = (Route) (incomingIntent.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

                if (route.getDate() == null) {
                    Log.e(TAG, "LocalDateTime is null");
                }

                Log.d(TAG, route.toString());

                boolean manuallyCreatedRoute = incomingIntent.getBooleanExtra
                        (WWRConstants.EXTRA_MANUALLY_CREATED_ROUTE_KEY, false);

                if (!manuallyCreatedRoute) {
                    // Save the newest walk
                    SharedPreferences sharedPreferences =
                            getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, route.getSteps());
                    editor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) route.getMiles());
                    editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, route.getDuration());
                    editor.apply();
                }


                // Save the newest walk
                SharedPreferences sharedPreferences =
                        getSharedPreferences(WWRConstants.SHARED_PREFERENCES_LAST_WALK_FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(WWRConstants.SHARED_PREFERENCES_LAST_WALK_STEPS_KEY, route.getSteps());
                editor.putFloat(WWRConstants.SHARED_PREFERENCES_LAST_WALK_MILES_KEY, (float) route.getMiles());

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(WWRConstants.DATE_FORMATTER_PATTERN);
                String formattedDate = route.getDate().format(dateTimeFormatter);

                editor.putString(WWRConstants.SHARED_PREFERENCES_LAST_WALK_DATE_KEY, formattedDate);
                editor.apply();
                Log.d(TAG, "Right before route insertion");
                mRouteViewModel.insert(route);
                break;
            case WWRConstants.EXTRA_HOME_SCREEN_ACTIVITY_CALLER_ID:
                break;
            default:
                Log.d(TAG, "caller is: " + callerId);
        }


        mAddNewRouteButton = findViewById(R.id.addNewRouteButton);
        mAddNewRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddNewRouteActivity();
            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.recycler_view_route);
        adapter = new RouteListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRouteClick(int position, List<Route> routes) {
        Route route = routes.get(position);
        startRouteDetailActivity(route);
    }

    /**
     * Starts the RouteDetailActivity
     *
     * @param route: the Route to start the detail activity for
     */
    private void startRouteDetailActivity(Route route) {
        Intent intent = new Intent(RoutesActivity.this, RouteDetailActivity.class);
        intent.putExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY, route);
        startActivityForResult(intent, START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    private void startAddNewRouteActivity() {
        Intent intent = new Intent(RoutesActivity.this, EnterWalkInformationActivity.class);
        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_ROUTES_ACTIVITY_CALLER_ID);
        startActivityForResult(intent, START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In method onActivityResult");

        // Check that this is the RouteDetailActivity calling back
        if (requestCode == START_ROUTE_DETAIL_ACTIVITY_REQUEST_CODE) {

            // If the RouteDetailActivity finished normally
            if (resultCode == Activity.RESULT_OK) {
                // Get the potentially updated Route object
                Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                // If the route object is null, then no updates should be made
                if (route == null) {
                    Log.d(TAG, "Route object returned in onActivityResult is null");
                } else {
                    // otherwise, update the appropriate route object
                    Log.d(TAG, route.toString());
                    mRouteViewModel.updateLastWalk(route);
                }

            } else if (requestCode == START_ADD_NEW_ROUTE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    // Add the new route
                    Route route = (Route) (data.getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));
                    Log.d(TAG, route.toString());
                    mRouteViewModel.insert(route);
                } else {
                    Log.d(TAG, "Activity result not OK from add new route");
                }

            } else {
                Log.d(TAG, "Result of RouteDetailActivity is (not OK): " + resultCode);

            }
        }
    }

    public void generateFakeRoute() {
        Route testRoute = new Route("route", "staring", null, "", 10, 10, null, true, "");
        mRouteViewModel.insert(testRoute);
    }

    public static void setIsTest(boolean isTest) {
        RoutesActivity.sIsTest = isTest;
    }
}
