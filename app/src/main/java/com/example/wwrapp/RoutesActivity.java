package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class RoutesActivity extends AppCompatActivity implements RecyclerViewAdapter.OnRouteListener{

    private static final String TAG = "RoutesActivity";

    private ArrayList<String> mRouteName = new ArrayList<>();
    private ArrayList<String> mRouteDate = new ArrayList<>();
    private ArrayList<String> mRouteMile = new ArrayList<>();
    private ArrayList<String> mRouteStep = new ArrayList<>();
    private ArrayList<Boolean> mFavourite = new ArrayList<>();

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
        mRouteName.add("Fake Route");
        mRouteDate.add("2000/01/01");
        mRouteStep.add("9999");
        mRouteMile.add("999.99");
        mFavourite.add(true);
        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.recyclerview_route);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,mRouteName,mRouteDate,mRouteMile,mRouteStep,mFavourite,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRouteClick(int position) {
        Intent intent = new Intent(this, WalkActivity.class);
        startActivity(intent);
    }
}
