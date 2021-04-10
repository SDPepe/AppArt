package ch.epfl.sdp.appart;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class VirtualTourUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public final ActivityScenarioRule<PanoramaActivity> vtourActivityRule =
            new ActivityScenarioRule<>(PanoramaActivity.class);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    @Ignore
    public void backButtonClosesActivity() {
        onView(withId(R.id.back_Panorama_button)).perform(click());
        assertEquals(Activity.RESULT_CANCELED, vtourActivityRule.getScenario().getResult()
                .getResultCode());
    }

}
