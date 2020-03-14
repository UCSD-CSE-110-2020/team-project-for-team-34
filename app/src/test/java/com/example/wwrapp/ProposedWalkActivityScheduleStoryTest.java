package com.example.wwrapp;

import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wwrapp.activities.ProposedWalkActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class ProposedWalkActivityScheduleStoryTest {

    @BeforeClass
    public static void disableFirestore() {
        ProposedWalkActivity.MOCKING = true;
    }

    @Rule
    public ActivityScenarioRule<ProposedWalkActivity> rule = new ActivityScenarioRule<>(ProposedWalkActivity.class);

    private ActivityScenario<ProposedWalkActivity> scenario;

    private Button mScheduleBtn;

    @Before
    public void setUp() {
        this.scenario = rule.getScenario();
    }

    @After
    public void clear() {
        scenario.close();
    }

    private void init(ProposedWalkActivity proposedWalkActivity) {
        mScheduleBtn = proposedWalkActivity.findViewById(R.id.scheduleBtn);
    }

    @Test
    public void testProposedWalkSchedule() {
        scenario.onActivity(proposedWalkActivity -> {
            init(proposedWalkActivity);
            assertEquals("Schedule Walk", mScheduleBtn.getText().toString());
        });
    }
}
