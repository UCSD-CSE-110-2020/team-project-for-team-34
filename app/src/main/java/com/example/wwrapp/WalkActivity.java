package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

public class WalkActivity extends AppCompatActivity {
    private TextView hrView, minView, secView;
    private Button stop;
    private TimerTask timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        hrView = findViewById(R.id.hrs);
        minView = findViewById(R.id.mins);
        secView = findViewById(R.id.secs);
        stop = findViewById(R.id.stopButton);
        timer = new TimerTask();
        timer.execute("0");
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel(false);
            }

        });
    }

    private class TimerTask extends AsyncTask<String,String, String> {
        private long time = 0;
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String ... params) {
            try {
                Thread.sleep(1000);
                time = time++;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return "Slept for 1 second";
        }

        @Override
        public void onProgressUpdate(String ... text) {
            progressDialog.dismiss();
            long hrTime = (time / 3600000);
            long minTime = (time / 60000) % 60;
            long secTime = (time / 1000) % 60;
            hrView.setText((int)hrTime + " Hr");
            hrView.setText((int)minTime + " Min");
            hrView.setText((int)secTime + "Sec");
        }
    }
}
