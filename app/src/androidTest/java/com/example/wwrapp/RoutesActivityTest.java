package com.example.wwrapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.dannyroa.espresso_samples.RecyclerViewMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RoutesActivityTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize() {
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
        RoutesActivity.setIsTest(true);
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Ignore
    @Test
    public void routesActivityTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.routeScreenButton),
                        isDisplayed()));
        appCompatButton.perform(click());


        onView(withRecyclerView(R.id.recycler_view_route).atPosition(0))
                .check(matches(hasDescendant(withText("route"))));

        onView(withRecyclerView(R.id.recycler_view_route).atPosition(0))
                .check(matches(hasDescendant(withText("staring"))));

        onView(withRecyclerView(R.id.recycler_view_route).atPosition(0))
                .check(matches(hasDescendant(withText("10"))));

        onView(withRecyclerView(R.id.recycler_view_route).atPosition(0))
                .check(matches(hasDescendant(withText("10.0"))));
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
