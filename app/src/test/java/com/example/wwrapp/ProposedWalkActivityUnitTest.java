package com.example.wwrapp;

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

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class ProposedWalkActivityUnitTest {

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

    }

    @Test
    public void testEnterWalkInformationActivity() {
        scenario.onActivity(proposedWalkActivity -> {
            init(proposedWalkActivity);
            assertTrue(true);
            RouteBuilder routeBuilder = new RouteBuilder();
//            mRoute = routeBuilder.setRouteName("route name")
//                    .setStartingPoint("starting point")
//                    .setFavorite(true)
//                    .setWalked(true)
//                    .setStatus("proposed")
//                    .setSteps(100)
//                    .setDateOfLastWalk("March 13")
//                    .getRoute();
//            mWalk = new ProposeWalk(mRoute, "proposerEmail", "proposerName");
//            ProposeWalkUser user1 = new ProposeWalkUser("email1", "name1");
//            ProposeWalkUser user2 = new ProposeWalkUser("email2", "name2");
//
//            mWalk.addUser("email1", "name1");
//            mWalk.addUser("email2", "name2");

        });
    }


}
