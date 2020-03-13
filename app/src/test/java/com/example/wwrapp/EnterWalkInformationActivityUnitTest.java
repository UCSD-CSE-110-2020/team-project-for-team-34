package com.example.wwrapp;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wwrapp.activities.EnterWalkInformationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class EnterWalkInformationActivityUnitTest {

    @Rule
    public ActivityScenarioRule<EnterWalkInformationActivity>scenarioRule = new ActivityScenarioRule<>(EnterWalkInformationActivity.class);
    private ActivityScenario<EnterWalkInformationActivity> scenario;

    private RadioGroup mRouteShapeRadioGroup;
    private RadioGroup mRouteElevationRadioGroup;
    private RadioGroup mRouteEnvironmentRadioGroup;
    private RadioGroup mRouteSmoothnessRadioGroup;
    private RadioGroup mRouteDifficultyRadioGroup;
    private RadioButton mRouteShapeRadioBtn;
    private RadioButton mRouteElevationRadioBtn;
    private RadioButton mRouteEnvironmentRadioBtn;
    private RadioButton mRouteSmoothnessRadioBtn;
    private RadioButton mRouteDifficultyRadioBtn;
    private RadioButton mRouteFavoriteRadioBtn;
    private EditText routeName;
    private EditText startingPoint;

    @Before
    public void setUp() {
        this.scenario = scenarioRule.getScenario();
    }

    @After
    public void clear() {
        scenario.close();
    }

    private void init(EnterWalkInformationActivity enterWalkInformationActivity){
        mRouteShapeRadioGroup = enterWalkInformationActivity.findViewById(R.id.route_shape_radio_group);
        mRouteElevationRadioGroup = enterWalkInformationActivity.findViewById(R.id.route_elevation_radio_group);
        mRouteEnvironmentRadioGroup = enterWalkInformationActivity.findViewById(R.id.route_environment_radio_group);
        mRouteSmoothnessRadioGroup = enterWalkInformationActivity.findViewById(R.id.route_smoothness_radio_group);
        mRouteDifficultyRadioGroup = enterWalkInformationActivity.findViewById(R.id.route_difficulty_radio_group);
        mRouteFavoriteRadioBtn= enterWalkInformationActivity.findViewById(R.id.favorite);
        routeName = enterWalkInformationActivity.findViewById(R.id.route_name_edit_text);
        startingPoint = enterWalkInformationActivity.findViewById(R.id.starting_point_edit_text);
    }

    @Test
    public void testEnterWalkInformationActivity() {
        scenario.onActivity(enterWalkInformationActivity -> {
            init(enterWalkInformationActivity);
            routeName.setText("village");
            String expectedRouteName = "village";
            assertEquals(expectedRouteName, routeName.getText().toString());
            startingPoint.setText("fuck me");
            String expectedStartingPoint = "fuck me";
            assertEquals(expectedStartingPoint, startingPoint.getText().toString());

        });
    }

    @Test
    public void testRadioButtonsActivity(){
        scenario.onActivity(enterWalkInformationActivity -> {
           init(enterWalkInformationActivity);
           mRouteFavoriteRadioBtn.setChecked(true);
           assertEquals(mRouteFavoriteRadioBtn.isChecked(), true);
           mRouteDifficultyRadioGroup.check(R.id.difficult);
           mRouteDifficultyRadioBtn = enterWalkInformationActivity.findViewById(mRouteDifficultyRadioGroup.getCheckedRadioButtonId());
           assertEquals(mRouteDifficultyRadioBtn.isChecked(), true);
           mRouteSmoothnessRadioGroup.check(R.id.even);
            mRouteSmoothnessRadioBtn = enterWalkInformationActivity.findViewById(mRouteSmoothnessRadioGroup.getCheckedRadioButtonId());
            assertEquals(mRouteSmoothnessRadioBtn.isChecked(), true);
           mRouteEnvironmentRadioGroup.check(R.id.trail);
            mRouteEnvironmentRadioBtn = enterWalkInformationActivity.findViewById(mRouteEnvironmentRadioGroup.getCheckedRadioButtonId());
            assertEquals(mRouteSmoothnessRadioBtn.isChecked(),true);
           assertEquals(mRouteElevationRadioGroup.getCheckedRadioButtonId(), -1);
        });
    }
}
