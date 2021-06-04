package ch.epfl.sdp.appart;


import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@HiltAndroidTest
@UninstallModules(DatabaseModule.class)
public class StepCounterActivityTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityTestRule<StepCounterActivity> mActivityTestRule = new ActivityTestRule<>(StepCounterActivity.class, false, false);

    @BindValue
    DatabaseService db = new MockDatabaseService();

    /* Used to grant permission always */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACTIVITY_RECOGNITION);

    @Test
    public void stepCounterActivityTest() {

        /* ================================================================================================================ */
        /*                                      FIRST BOOT OF THE ACTIVITY - STEP COUNT IS 0                                */
        /* ================================================================================================================ */

        Intent justInitializedStepCounterActivity = new Intent();
        mActivityTestRule.launchActivity(justInitializedStepCounterActivity);


        ViewInteraction textView = onView(
                allOf(withId(R.id.stepsToHomeSentence_StepCounter_TextView), withText("How many steps till home?"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText("How many steps till home?")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.numberOfSteps_StepCounter_TextView), withText("0"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("0")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.km_StepCounter_TextView), withText("~ 0 meters"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView3.check(matches(withText("~ 0 meters")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.stepsFromLastBootSentence_StepCounter_TextView), withText("steps done from last boot"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView5.check(matches(withText("steps done from last boot")));

        /* closing first boot of the activity */
        onView(withId(R.id.closeStepCount_StepCounter_Button)).perform(click());



        /* ================================================================================================================ */
        /*               NEW BOOT OF THE ACTIVITY - onSensorChanged gets mocked for a total of 50 steps                     */
        /* ================================================================================================================ */

        Intent updatedStepCounterActivity = new Intent();
        updatedStepCounterActivity.putExtra(String.valueOf(ActivityCommunicationLayout.ANDROID_TEST_IS_RUNNING), ActivityCommunicationLayout.ANDROID_TEST_IS_RUNNING);
        mActivityTestRule.launchActivity(updatedStepCounterActivity);

        /* ================================================================================================================ */
        /*                                    Starting Count - the mock is done in-activity                                 */
        /* ================================================================================================================ */

        String FINAL_STEP_COUNT_AFTER_MOCK = "50";
        String FINAL_METER_COUNT_AFTER_MOCK = "~ 33 meters";


        ViewInteraction appCompatButtonS = onView(
                allOf(withId(R.id.startStepCount_StepCounter_Button), withText("Start"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatButtonS.perform(click());

        ViewInteraction progressBar = onView(
                allOf(withId(R.id.progress_StepCounter_ProgressBar),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        progressBar.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.stopStepCount_StepCounter_Button), withText("Stop"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButtonStop = onView(
                allOf(withId(R.id.stopStepCount_StepCounter_Button), withText("Stop"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                11),
                        isDisplayed()));
        appCompatButtonStop.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.stepsToHomeSentence_StepCounter_TextView), withText("How many steps till home?"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView6.check(matches(withText("How many steps till home?")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.numberOfSteps_StepCounter_TextView), withText(FINAL_STEP_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.numberOfSteps_StepCounter_TextView), withText(FINAL_STEP_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView8.check(matches(withText(FINAL_STEP_COUNT_AFTER_MOCK)));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.km_StepCounter_TextView), withText(FINAL_METER_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.km_StepCounter_TextView), withText(FINAL_METER_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView10.check(matches(withText(FINAL_METER_COUNT_AFTER_MOCK)));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.totalNumberOfSteps_StepCounter_TextView), withText(FINAL_STEP_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView11.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.totalNumberOfSteps_StepCounter_TextView), withText(FINAL_STEP_COUNT_AFTER_MOCK),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView12.check(matches(withText(FINAL_STEP_COUNT_AFTER_MOCK)));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.stepsFromLastBootSentence_StepCounter_TextView), withText("steps done from last boot"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        textView13.check(matches(withText("steps done from last boot")));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.closeStepCount_StepCounter_Button), withText("Close"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(ViewGroup.class)))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        onView(withId(R.id.closeStepCount_StepCounter_Button)).perform(click());
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



