package com.example.wwrapp;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.wwrapp.activities.HomeScreenActivity;

import com.example.wwrapp.activities.TeamActivity;

import com.example.wwrapp.activities.RoutesActivity;

import com.example.wwrapp.activities.TeamRoutesActivity;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TeammateRouteDetailedActivityTest {
    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize(){
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);

        //TeamRoutesActivity.setTestTeammateRoute(true);
        HomeScreenActivity.disableUser(true);
        TeamActivity.disableUser(true);
        TeamRoutesActivity.disableUser(true);
    }


    @Test
    public void teammateRouteDetailedTest(){
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.routeScreenButton), withText("Go to Routes")));
        appCompatButton1.perform(click());


        ViewInteraction button = onView(
                allOf(withId(R.id.teammateRouteBtn),
                        isDisplayed()));
        button.check(matches(isDisplayed()));


        // TODO: Add more since right now teammate route is just a place holder.


    }
}
