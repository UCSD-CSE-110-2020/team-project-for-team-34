package com.example.wwrapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
public class RoutesDetailActivityTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize(){
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
        RoutesActivity.setIsTest(true);
    }

    @Test
    public void routesDetailActivityTest() {

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.routeScreenButton), withText("Go to Routes"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onView(withId(R.id.recycler_view_route)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        /*ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.parent_layout),
                        isDisplayed()));
        relativeLayout.perform(click());*/

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.favoriteBtnDetail),
                        isDisplayed()));
        toggleButton.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.route_detail_name), withText("route"),

                        isDisplayed()));
        textView.check(matches(withText("route")));

        ViewInteraction textView2 = onView(
                allOf(withText("Starting Point: "),
                        isDisplayed()));
        textView2.check(matches(withText("Starting Point: ")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.starting_point_text_view), withText("staring"),
                        isDisplayed()));
        textView3.check(matches(withText("staring")));

        ViewInteraction textView4 = onView(
                allOf(withText("Steps: "),
                        isDisplayed()));
        textView4.check(matches(withText("Steps: ")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.steps_text_view), withText("10"),
                        isDisplayed()));
        textView5.check(matches(withText("10")));

        ViewInteraction textView6 = onView(
                allOf(withText("Miles: "),
                        isDisplayed()));
        textView6.check(matches(withText("Miles: ")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.miles_text_view), withText("10.0"),
                        isDisplayed()));
        textView7.check(matches(withText("10.0")));

        ViewInteraction textView8 = onView(
                allOf(withText("Date:"),
                        isDisplayed()));
        textView8.check(matches(withText("Date:")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.route_detail_date),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withText("Tags:"),
                        isDisplayed()));
        textView10.check(matches(withText("Tags:")));

        ViewInteraction textView11 = onView(
                allOf(withText("Notes: "),
                        isDisplayed()));
        textView11.check(matches(withText("Notes: ")));

        ViewInteraction button = onView(
                allOf(withId(R.id.editBtn),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.notes_text_view),
                        isDisplayed()));
        textView12.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.start_existing_walk_btn),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.close_route_btn),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.recycler_view_route),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
