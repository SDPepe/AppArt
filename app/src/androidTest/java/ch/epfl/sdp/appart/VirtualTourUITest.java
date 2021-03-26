package ch.epfl.sdp.appart;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.virtualtour.PanoramaGlActivity;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@UninstallModules(FireBaseModule.class)
@HiltAndroidTest
public class VirtualTourUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule  = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule vtourActivityRule =
            new ActivityScenarioRule<>(PanoramaGlActivity.class);

    @BindValue
    Database database = new MockDataBase();

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void backButtonClosesActivity(){
        onView(withId(R.id.VrTourBackButton)).perform(click());
        assertEquals(Activity.RESULT_CANCELED, vtourActivityRule.getScenario().getResult()
                .getResultCode());
    }

}
