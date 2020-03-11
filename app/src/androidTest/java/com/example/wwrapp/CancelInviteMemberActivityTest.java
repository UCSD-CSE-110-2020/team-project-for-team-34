package com.example.wwrapp;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.wwrapp.activities.HomeScreenActivity;

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
public class CancelInviteMemberActivityTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> mActivityTestRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @BeforeClass
    public static void initialize(){
        HomeScreenActivity.setIgnoreHeight(true);
        HomeScreenActivity.setEnableFitnessRunner(false);
    }

    @Test
    public void inviteMemberActivityTest(){
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.teamScreenButton), withText("Go To Team"),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addNewTeamButton), withText("+"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText1 = onView(
                allOf(withId(R.id.member_name_edit_text),
                        isDisplayed()));
        appCompatEditText1.perform(replaceText("Ellen"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.member_email_edit_text),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("ellen@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.add_member_cancel_button), withText("Cancel"),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction relativeLayout = onView(allOf(withId(R.id.recycler_view_team), isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

    }
}
