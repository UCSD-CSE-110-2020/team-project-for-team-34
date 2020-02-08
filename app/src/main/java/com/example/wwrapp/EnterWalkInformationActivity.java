package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterWalkInformationActivity extends AppCompatActivity {

    private static String ENTER_ROUTE_NAME_TOAST = "Please enter the route name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_walk_information);

        final EditText routeName = findViewById(R.id.route_name_edit_text);
        EditText startingPoint = findViewById(R.id.starting_point_edit_text);

        Button doneBtn = findViewById(R.id.enter_walk_info_done_button);
        doneBtn.setEnabled(false);


        doneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (routeName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), ENTER_ROUTE_NAME_TOAST, Toast.LENGTH_LONG).show();
                } else {
                    // Save data and go to routes screen
                    Toast.makeText(getApplicationContext(), "Save data and go to routes screen", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelBtn = findViewById(R.id.enter_walk_info_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Go to home screen
                Toast.makeText(getApplicationContext(), "Save data and go to home screen", Toast.LENGTH_LONG).show();

            }
        });
    }
}
