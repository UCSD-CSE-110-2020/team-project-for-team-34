package com.example.wwrapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.utils.WWRConstants;

/**
 * Prompts the user for their height and saves the measurements. This activity is launched by
 * the Home screen if the user has never entered their height before.
 */
public class HeightScreenActivity extends AppCompatActivity {

    private Spinner mFeetSpinner;
    private Spinner mInchesSpinner;
    private Button mDoneBtn;
    private static final String[] FEET_VALUES = {"", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] INCH_VALUES =
            {"", "0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};

    // Toast messages
    public static String VALID_HEIGHT_TOAST_TEXT = "Height saved!";
    public static String INVALID_HEIGHT_TOAST_TEXT = "Please enter a valid height";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        mDoneBtn = (Button) findViewById(R.id.height_button);

        mFeetSpinner = (Spinner) findViewById(R.id.spinner_feet);

        final ArrayAdapter<String> feetAdapter = new ArrayAdapter<String>(HeightScreenActivity.this,
                android.R.layout.simple_spinner_item, FEET_VALUES);

        mInchesSpinner = (Spinner) findViewById(R.id.spinner_inch);

        ArrayAdapter<String> inchAdapter = new ArrayAdapter<String>(HeightScreenActivity.this,
                android.R.layout.simple_spinner_item, INCH_VALUES);

        feetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mFeetSpinner.setAdapter(feetAdapter);

        inchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mInchesSpinner.setAdapter(inchAdapter);

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inches = mInchesSpinner.getSelectedItem().toString();
                String feet = mFeetSpinner.getSelectedItem().toString();
                if(inches.isEmpty() || feet.isEmpty()) {
                    Toast.makeText(HeightScreenActivity.this,
                            INVALID_HEIGHT_TOAST_TEXT
                            ,Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences saveHeight = getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = saveHeight.edit();
                    editor.putInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, Integer.parseInt(inches));
                    editor.putInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, Integer.parseInt(feet));
                    editor.apply();
                    Toast.makeText(HeightScreenActivity.this,
                            VALID_HEIGHT_TOAST_TEXT, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
