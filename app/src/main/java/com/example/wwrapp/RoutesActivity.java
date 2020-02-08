package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class RoutesActivity extends AppCompatActivity {

    private static final String TAG = "RoutesActivity";

    private ArrayList<String> mRouteName = new ArrayList<>();
    private ArrayList<String> mRouteDate = new ArrayList<>();
    private ArrayList<String> mRouteMile = new ArrayList<>();
    private ArrayList<String> mRouteStep = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d(TAG, "onCreate: started");

        loadRoutes();
    }

    private void loadRoutes(){
        Log.d(TAG, "loadRoutes: loading routes");
        //read Route into the arraylist
        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.recyclerview_route);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mRouteName,mRouteDate,mRouteMile,mRouteStep,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
