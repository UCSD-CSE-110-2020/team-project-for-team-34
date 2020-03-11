package com.example.wwrapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wwrapp.R;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.MockUser;
import com.example.wwrapp.models.ProposeWalk;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Team;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;


public class ProposeWalkScreenActivity extends AppCompatActivity {

    public static String TAG = "ProposeWalkScreenActivity";
    private static int DEFAULT_VALUE = -1;
    private static int AFTER_CURRENT_DATE = 1;
    private static String INVALID_DATE_STRING = "Please enter a valid date";
    private static String INVALID_SEND_STRING = "Please enter a valid date/time";
    private static String PM = "pm";
    private static String AM = "am";

    private TextView timeInput;
    private TextView dateInput;
    private static TextView dateShow;
    private static TextView timeShow;
    private Button sendButton;

    private Route mRoute;
    private FirebaseFirestore mFirestore;

    private static int mYear = DEFAULT_VALUE;
    private static int mMonth = DEFAULT_VALUE;
    private static int mDay = DEFAULT_VALUE;
    private static int mHour = DEFAULT_VALUE;
    private static int mMinutes = DEFAULT_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_walk_screen);

        mFirestore = FirebaseFirestore.getInstance();

        dateInput = findViewById(R.id.proposeDateInput);
        timeInput = findViewById(R.id.proposeTimeInput);
        dateShow = findViewById(R.id.proposeDateString);
        timeShow = findViewById(R.id.proposeTimeString);
        sendButton = findViewById(R.id.send_button);

        mRoute = (Route) (getIntent().getSerializableExtra(WWRConstants.EXTRA_ROUTE_OBJECT_KEY));

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDateValid()) {
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Team team = document.toObject(Team.class);
                                            team.setProposeWalk(mRoute);
                                            //TODO: IDK HOW THE TEAMS DATA SCEHEMA WORKS
                                            mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                                    .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH).set(team)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            finish();
                                                            Log.d(TAG, "Team Proposed Walk Updated1");
                                                        }
                                                    });
                                        } else {
                                            Log.d(TAG, "Could not find team");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(ProposeWalkScreenActivity.this,
                            INVALID_SEND_STRING, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        public int HOURS_IN_HALF_DAY = 12;
        public int START_HOUR_OF_DAY = 0;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ProposeWalkScreenActivity.mHour = hourOfDay;
            ProposeWalkScreenActivity.mMinutes = minute;
            String time;
            if(hourOfDay > HOURS_IN_HALF_DAY ) {
                hourOfDay = hourOfDay - HOURS_IN_HALF_DAY;
                time = hourOfDay + ":" + minute + PM;
            }
            else if( hourOfDay == HOURS_IN_HALF_DAY ) {
                time = hourOfDay + ":" + minute + PM;
            }
            else if( hourOfDay == START_HOUR_OF_DAY ) {
                hourOfDay = HOURS_IN_HALF_DAY;
                time = hourOfDay + ":" + minute + AM;
            }
            else {
                time = hourOfDay + ":" + minute + AM;
            }
            timeShow.setText(time);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendarNow = Calendar.getInstance();
            Date now = new Date();
            calendarNow.setTime(now);
            Calendar calendarProposed = Calendar.getInstance();
            calendarProposed.set(year,month,day);
            if(calendarProposed.compareTo(calendarNow) == AFTER_CURRENT_DATE){
                ProposeWalkScreenActivity.mYear = year;
                ProposeWalkScreenActivity.mMonth = month;
                ProposeWalkScreenActivity.mDay = day;
                String date = month+"/"+ day + "/"+year;
                dateShow.setText(date);
            }
            else {
                Toast.makeText(getContext(),
                        INVALID_DATE_STRING, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private boolean isDateValid() {
        if( mYear == DEFAULT_VALUE || mMonth == DEFAULT_VALUE || mDay == DEFAULT_VALUE ||
                mHour == DEFAULT_VALUE || mMinutes == DEFAULT_VALUE ) {
            return false;
        }
        else {
            return true;
        }
    }
}
