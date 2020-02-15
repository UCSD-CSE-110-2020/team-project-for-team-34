package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.wwrapp.database.Route;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private void readFromRoute() {
        Route route = (Route) (getIntent().getSerializableExtra(RoutesActivity.ROUTE_KEY));

        TextView routeNameText = findViewById(R.id.route_detail_name);
        routeNameText.setText(route.getRouteName());

        TextView startingPointTezt = findViewById(R.id.starting_point_text_view);
        startingPointTezt.setText(route.getStartingPoint());

        LocalDateTime routeDate = route.getDate();
        if (routeDate == null) {
            routeDate = LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String formattedDate = routeDate.format(formatter);
        TextView routeDateText = findViewById(R.id.route_detail_date);
        routeDateText.setText(formattedDate);

        double miles = route.getMiles();
        TextView routeMilesText = findViewById(R.id.miles_text_view);
        routeMilesText.setText(String.valueOf(miles));

        long steps = route.getSteps();
        TextView routeStepsText = findViewById(R.id.steps_text_view);
        routeStepsText.setText(String.valueOf(steps));

        TextView noteText = findViewById(R.id.notes_text_view);
        noteText.setText(route.getNotes());

        List<String> tags = route.getTags();
        if (tags != null) {
            int i = 1;
            for (String tag : tags) {
                TextView tagText;
                switch (i) {
                    case 1:
                        tagText = findViewById(R.id.tag1);
                        tagText.setText(tag);
                        break;
                    case 2:
                        tagText = findViewById(R.id.tag2);
                        tagText.setText(tag);
                        break;
                    case 3:
                        tagText = findViewById(R.id.tag3);
                        tagText.setText(tag);
                        break;
                    case 4:
                        tagText = findViewById(R.id.tag4);
                        tagText.setText(tag);
                        break;
                    case 5:
                        tagText = findViewById(R.id.tag5);
                        tagText.setText(tag);
                        break;
                    default:
                        break;
                }
                i++;
            }
        }
        Context current = this;
        Boolean isFavorite = route.isFavorite();
        favoriteBtn = findViewById(R.id.favoriteBtnDetail);
        if (isFavorite) {
            favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_on));
            favoriteBtn.setChecked(true);
        } else {
            favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_off));
        }
        favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_on));
                }
                else
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current, R.drawable.ic_star_off));
            }
        });

        /*favoriteBtn = findViewById(R.id.favoriteBtn);
        Context current = this;
        favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current,R.drawable.ic_star_on));
                else
                    favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(current,R.drawable.ic_star_off));
            }
        });*/
    }
}
