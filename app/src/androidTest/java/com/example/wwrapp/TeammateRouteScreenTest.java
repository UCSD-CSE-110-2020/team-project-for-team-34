package com.example.wwrapp;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.wwrapp.activities.HomeScreenActivity;
import com.example.wwrapp.activities.RoutesActivity;
import com.example.wwrapp.activities.TeamRoutesActivity;

import org.junit.BeforeClass;
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
public class TeammateRouteScreenTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize() {
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
        RoutesActivity.setIsTest(true);
    }

    @Test
    public void NoTeammateRoutesTest() {
        mActivityTestRule.getActivity();
        TeamRoutesActivity.IS_TESTING_EMPTY = true;
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.routeScreenButton),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.teammateRouteBtn),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatView = onView(
                allOf(withId(R.id.emptyStringView),
                        isDisplayed()));
        appCompatView.check(matches(withText("None of your teammates have routes :(")));
    }
}
