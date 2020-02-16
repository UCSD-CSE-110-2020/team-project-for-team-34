package com.example.wwrapp;

import android.widget.Button;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = Config.OLDEST_SDK)
public class WalkActivityTest {
    @Rule
    public ActivityScenarioRule<WalkActivity> scenarioRule =
            new ActivityScenarioRule<>(WalkActivity.class);

    private ActivityScenario<WalkActivity> scenario;

    private Spinner feetSpinner;
    private Spinner inchSpinner;
    private Button doneBtn;

    @Before
    public void setup() {
        this.scenario = scenarioRule.getScenario();
    }
}
