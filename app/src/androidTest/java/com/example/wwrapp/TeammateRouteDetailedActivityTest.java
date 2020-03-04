package com.example.wwrapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
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
        TeammateRouteActivity.setTestTeammateRoute(true);
    }

    @Test
    public void teammateRouteDetailedTest(){
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.routeScreenButton), withText("Go to Routes"),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.teammateRouteBtn), withText("Teammate Routes"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        // TODO: Add more since right now teammate route is just a place holder.


    }
}
