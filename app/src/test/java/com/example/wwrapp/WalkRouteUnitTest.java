package com.example.wwrapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = Config.OLDEST_SDK)
public class WalkRouteUnitTest {
    @Rule
    public ActivityScenarioRule<WalkActivity> scenarioRule =
            new ActivityScenarioRule<>(WalkActivity.class);

    private ActivityScenario<WalkActivity> scenario;
    private TextView stepCount, mileCount, hrs, mins, secs;
    private int heightFeet, heightInches;
    private Context context;

    private static final String STEP_COUNT_DEFAULT = "0";
    private static final String MILE_COUNT_DEFAULT = "That's 0.0 miles so far";
    private static final String HRS_DEFAULT = "0 hr";
    private static final String MINS_DEFAULT = "0 min";
    private static final String SECS_DEFAULT = "0 sec";
    private static final double ACCEPTABLE_ERROR = 0.001;


    @Before
    public void setup() {
        this.scenario = scenarioRule.getScenario();
        heightFeet = 5;
        heightInches = 3;
        context = InstrumentationRegistry.getInstrumentation().getContext();
    }

    private void init(WalkActivity walkActivity) {
        stepCount = walkActivity.findViewById(R.id.stepCount);
        mileCount = walkActivity.findViewById(R.id.mileCount);
        hrs = walkActivity.findViewById(R.id.hrs);
        mins = walkActivity.findViewById(R.id.mins);
        secs = walkActivity.findViewById(R.id.secs);
    }

    @Test
    public void startUpValid() {
        scenario.onActivity(walkActivity -> {
            init(walkActivity);
            assertEquals(stepCount.getText().toString(),STEP_COUNT_DEFAULT);
            assertEquals(mileCount.getText().toString(),MILE_COUNT_DEFAULT);
            assertEquals(hrs.getText().toString(),HRS_DEFAULT);
            assertEquals(mins.getText().toString(),MINS_DEFAULT);
            assertEquals(secs.getText().toString(),MINS_DEFAULT);
        });
    }

    @Test
    public void FakeWalk() {
        SharedPreferences mStepsSharedPreference;
        mStepsSharedPreference = context.getSharedPreferences(HomeScreenActivity.STEPS_SHARED_PREF_NAME, MODE_PRIVATE);
        int testSteps = 2435;
        StepsAndMilesConverter converter = new StepsAndMilesConverter(heightFeet, heightInches);
        float expectedMiles = (float)(Math.round(4.627998737 * 10)/ 10.0);
        float testMiles = (float)(Math.round(converter.getNumMiles(testSteps) * 10)/ 10.0);
        assertEquals(expectedMiles, testMiles, ACCEPTABLE_ERROR);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(hrs.getText().toString(),"0 hr");
        assertEquals(mins.getText().toString(),"0 min");
        assertEquals(secs.getText().toString(),"1 sec");
    }
}
