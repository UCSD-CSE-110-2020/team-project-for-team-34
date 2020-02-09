package com.example.wwrapp;

import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = Config.OLDEST_SDK)
public class HomeActivityUnitTest {

    @Rule
    public ActivityScenarioRule<HomeScreenActivity> scenarioRule =
            new ActivityScenarioRule<>(HomeScreenActivity.class);

    private ActivityScenario<HomeScreenActivity> scenario;

    private TextView stepsTextView;
    private TextView milesTextView;

    private TextView lastStepsTextView;
    private TextView lastMilesTextView;
    private TextView lastTimeTextView;


    @Before
    public void setup() {
        this.scenario = scenarioRule.getScenario();
    }

    @After
    public void cleanup() {
        scenario.close();
    }

    private void init(HomeScreenActivity homeScreenActivity) {
        stepsTextView = homeScreenActivity.findViewById(R.id.homeSteps);
        milesTextView = homeScreenActivity.findViewById(R.id.homeMiles);

        lastStepsTextView = homeScreenActivity.findViewById(R.id.lastWalkSteps);
        lastMilesTextView = homeScreenActivity.findViewById(R.id.lastWalkDistance);
        lastTimeTextView = homeScreenActivity.findViewById(R.id.lastWalkTime);
    }

    @Test
    public void testSteps() {
        scenario.onActivity(homeScreenActivity -> {
            init(homeScreenActivity);
            long steps = 2435;
            homeScreenActivity.setStepCount(steps);
            assertEquals(steps, Integer.parseInt(stepsTextView.getText().toString()));
        });
    }

    @Test
    public void testMiles() {
        scenario.onActivity(homeScreenActivity -> {
            init(homeScreenActivity);
            long steps = 2435;
            int feet = 5;
            int inches = 3;
            homeScreenActivity.setHeight(feet, inches);
            homeScreenActivity.setStepCount(steps);
            double acceptableError = 0.001;
            double expectedMiles = 1.0;
            assertEquals(expectedMiles, Double.parseDouble(stepsTextView.getText().toString()), acceptableError);
        });
    }

    @Test
    public void testNoLastWalk() {
        scenario.onActivity(homeScreenActivity -> {
            init(homeScreenActivity);
            int expectedSteps = 0;
            double expectedMiles = 0.0;
            double acceptableError = 0.001;
            String expectedTime = "No time";
            assertEquals(expectedSteps, Integer.parseInt(lastStepsTextView.getText().toString()));
            assertEquals(expectedMiles, Double.parseDouble(lastMilesTextView.getText().toString()), acceptableError);
            assertEquals(expectedTime, lastTimeTextView.getText().toString());
        });
    }
}
