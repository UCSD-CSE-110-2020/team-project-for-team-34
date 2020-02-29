package com.example.wwrapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_team);
        final TeamListAdapter teamListAdapter = new TeamListAdapter(this);
        recyclerView.setAdapter(teamListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
