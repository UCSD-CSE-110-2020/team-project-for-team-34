package com.example.wwrapp;

import android.view.View;
import android.widget.TextView;

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
public class ScheduledWalkStoryTest {

    @BeforeClass
    public static void disableFirestore() {
        ProposedWalkActivity.MOCKING = true;
    }

    @Rule
    public ActivityScenarioRule<ProposedWalkActivity> rule = new ActivityScenarioRule<>(ProposedWalkActivity.class);

    private ActivityScenario<ProposedWalkActivity> scenario;

    private TextView mStartingPoint;

    @Before
    public void setUp() {
        this.scenario = rule.getScenario();
    }

    @After
    public void clear() {
        scenario.close();
    }

    private void init(ProposedWalkActivity proposedWalkActivity) {
        mStartingPoint = proposedWalkActivity.findViewById(R.id.starting_point_text_view);
    }

    @Test
    public void testProposedWalkGoogleMaps() {
        scenario.onActivity(proposedWalkActivity -> {
            init(proposedWalkActivity);
            assertEquals(View.VISIBLE, mStartingPoint.getVisibility());
        });
    }
}
