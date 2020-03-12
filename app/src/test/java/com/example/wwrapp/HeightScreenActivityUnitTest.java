package com.example.wwrapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wwrapp.activities.HeightScreenActivity;
import com.example.wwrapp.utils.WWRConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class HeightScreenActivityUnitTest {

    @Rule
    public ActivityScenarioRule<HeightScreenActivity> scenarioRule =
            new ActivityScenarioRule<>(HeightScreenActivity.class);

    private ActivityScenario<HeightScreenActivity> scenario;

    private Spinner feetSpinner;
    private Spinner inchSpinner;
    private Button doneBtn;

    @Before
    public void setup() {
        this.scenario = scenarioRule.getScenario();
    }

    @After
    public void clearHeightSharedPreferences() {
        scenario.onActivity(heightScreenActivity -> {
            SharedPreferences sharedPreferences =
                    heightScreenActivity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().commit();
        });
        scenario.close();
    }

    private void init(HeightScreenActivity heightScreenActivity) {
        feetSpinner = heightScreenActivity.findViewById(R.id.spinner_feet);
        inchSpinner = heightScreenActivity.findViewById(R.id.spinner_inch);
        doneBtn = heightScreenActivity.findViewById(R.id.height_button);
    }

    @Test
    public void testEnterAndSaveHeight() {
        scenario.onActivity(heightScreenActivity -> {
            init(heightScreenActivity);
            // Select height
            feetSpinner.setSelection(5);
            String expectedFeet = "5";
            assertEquals(expectedFeet, feetSpinner.getSelectedItem());
            inchSpinner.setSelection(4);
            String expectedInches = "3";
            assertEquals(expectedInches, inchSpinner.getSelectedItem());


            // Press done button
            doneBtn.performClick();

            // Check that data is saved
            SharedPreferences sharedPreferences =
                    heightScreenActivity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, Context.MODE_PRIVATE);
            int sharedPrefFeet =
                    sharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, -1);
            assertEquals(Integer.parseInt(expectedFeet), sharedPrefFeet);
            int sharedPrefInches =
                    sharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, -1);
            assertEquals(Integer.parseInt(expectedInches), sharedPrefInches);

            // Check that the activity has transitioned.
            String successToast = ShadowToast.getTextOfLatestToast();
            assertEquals(HeightScreenActivity.VALID_HEIGHT_TOAST_TEXT, successToast);

        });
    }

    @Test
    public void testEnterInvalidHeight() {
        scenario.onActivity(heightScreenActivity -> {
            init(heightScreenActivity);
            // Select height
            feetSpinner.setSelection(0);
            String expectedFeet = "";
            assertEquals(expectedFeet, feetSpinner.getSelectedItem());
            inchSpinner.setSelection(4);
            String expectedInches = "3";
            assertEquals(expectedInches, inchSpinner.getSelectedItem());

            // Press done button
            doneBtn.performClick();

            // Check that no data is saved
            SharedPreferences sharedPreferences =
                    heightScreenActivity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_HEIGHT_FILE_NAME, Context.MODE_PRIVATE);
            int sharedPrefFeet =
                    sharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_FEET_KEY, -1);
            assertEquals(-1, sharedPrefFeet);
            int sharedPrefInches =
                    sharedPreferences.getInt(WWRConstants.SHARED_PREFERENCES_HEIGHT_INCHES_KEY, -1);
            assertEquals(-1, sharedPrefInches);

            // Check that the current activity is still running
            String warningToast = ShadowToast.getTextOfLatestToast();
            assertEquals(HeightScreenActivity.INVALID_HEIGHT_TOAST_TEXT, warningToast);
        });
    }
}
