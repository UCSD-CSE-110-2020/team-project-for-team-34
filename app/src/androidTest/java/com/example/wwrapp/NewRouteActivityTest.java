package com.example.wwrapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewRouteActivityTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize(){
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
    }

    @Test
    public void newRouteActivityTest(){
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.routeScreenButton), withText("Go to Routes"),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addNewRouteButton), withText("+"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.new_route_name_edit_text),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("village"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.new_route_starting_point_edit_text),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("kitchen"), closeSoftKeyboard());

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.out_new), withText("out-and-back"),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.trail_new), withText("trail"),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.enter_new_route_info_done_button), withText("Done"),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction relativeLayout = onView(allOf(withId(R.id.recycler_view_route), isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));
    }
}
