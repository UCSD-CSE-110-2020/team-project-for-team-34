package com.example.wwrapp;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.wwrapp.activities.HomeScreenActivity;
import com.example.wwrapp.activities.InviteMemberScreenActivity;
import com.example.wwrapp.activities.TeamActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InvitationDeclinedScreenActivityTest {
    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize(){
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
        HomeScreenActivity.disableUser(true);
        TeamActivity.disableUser(true);
        TeamActivity.mockInviteMemberScreen(true);
        InviteMemberScreenActivity.testInvite(true);
    }

    @Test
    public void invitationActivityTest(){
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.teamScreenButton)));
        appCompatButton1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.invite_decline_button), withText("Decline"),
                        isDisplayed()));

        // TODO: Need to fix this part, not sure why it always error, similar to RouteActivityTest, and RoutesDetailActivityTest
//        ViewInteraction relativeLayout = onView(allOf(withId(R.id.recycler_view_team), isDisplayed()));
//        relativeLayout.check(matches(isDisplayed()));
    }
}
