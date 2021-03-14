package ch.epfl.sdp.appart;


import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@HiltAndroidTest
public final class HiltTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Inject
    Database database;

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void happyPath() {
        // Can already use analyticsAdapter here.
    }



}
