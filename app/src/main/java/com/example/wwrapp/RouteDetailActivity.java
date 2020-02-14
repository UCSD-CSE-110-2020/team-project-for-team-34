package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class RouteDetailActivity extends AppCompatActivity {

    ToggleButton favoriteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        findViewById(R.id.close_route_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        readFromRoute();
    }

    private void readFromRoute(){
        favoriteBtn = findViewById(R.id.favoriteBtn);
        Context current = this;
        favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current,R.drawable.ic_star_on));
                else
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current,R.drawable.ic_star_off));
            }
        });
    }
}
