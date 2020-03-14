package com.example.wwrapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wwrapp.activities.ProposedWalkActivity;
import com.example.wwrapp.models.RouteBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class ProposedWalkActivityUnitTest {

    private Button withDrawButton;
    private Button acceptButton;
    private Button badTimeButton;
    private Button badRouteButton;

    private TextView timeView;
    private TextView dateView;

    @BeforeClass
    public static void disableFirestore() {
        ProposedWalkActivity.MOCKING = true;
    }

    @Rule
    public ActivityScenarioRule<ProposedWalkActivity> rule = new ActivityScenarioRule<ProposedWalkActivity>(ProposedWalkActivity.class);

    private ActivityScenario<ProposedWalkActivity> scenario;

    @Before
    public void setUp() {
        this.scenario = rule.getScenario();
    }

    @After
    public void clear() {
        scenario.close();
    }

    private void init(ProposedWalkActivity proposedWalkActivity) {
        RouteBuilder routeBuilder = new RouteBuilder();
        withDrawButton = proposedWalkActivity.findViewById(R.id.withdrawBtn);
        timeView = proposedWalkActivity.findViewById(R.id.proposedDateTextView);
        dateView = proposedWalkActivity.findViewById(R.id.proposedTimeTextView);
        acceptButton = proposedWalkActivity.findViewById(R.id.acceptBtn);
        badTimeButton = proposedWalkActivity.findViewById(R.id.badTimeBtn);
        badRouteButton = proposedWalkActivity.findViewById(R.id.badRouteBtn);
    }

    @Test
    public void testTimeAndDateTextViewsVisible() {
        scenario.onActivity(proposedWalkActivity -> {
            init(proposedWalkActivity);
            assertTrue(dateView != null);
            assertTrue(timeView != null);
        });
    }

    @Test
    public void testWithDrawButton() {
        scenario.onActivity(proposedWalkActivity -> {
            init(proposedWalkActivity);
            assertTrue(withDrawButton != null && withDrawButton.getVisibility() == View.VISIBLE);
            assertEquals("Withdraw Walk", withDrawButton.getText());
        });
    }
}
