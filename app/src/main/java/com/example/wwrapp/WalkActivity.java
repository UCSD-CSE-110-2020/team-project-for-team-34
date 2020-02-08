package com.example.wwrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

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

        @Override
        protected String doInBackground(String ... params) {
            while (true) {
                try {
                    Thread.sleep(1000);
                    time = ++time;
                    publishProgress("update Time");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }

        @Override
        public void onProgressUpdate(String ... text) {
            long hrTime = (time / 3600);
            long minTime = (time / 60) % 60;
            long secTime = (time) % 60;
            hrView.setText((int)hrTime + " hr");
            minView.setText((int)minTime + " min");
            secView.setText((int)secTime + " sec");
        }
    }
}
