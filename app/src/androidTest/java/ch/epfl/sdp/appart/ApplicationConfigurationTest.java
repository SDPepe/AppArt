package ch.epfl.sdp.appart;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public class ApplicationConfigurationTest {

    /*@Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Inject
    public ApplicationConfiguration configuration;

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    private static class Dummy1 {}
    private static class Dummy2 {}

    @Test
    public void CheckDemoModeSelectsRightClass() {
        mainActivityRule.getScenario().onActivity(activity -> configuration.setDemoMode(activity, false));
        Class<?> selected1 = configuration.demoModeSelector(Dummy1.class, Dummy2.class);
        assertEquals(selected1, Dummy1.class);
        assertFalse(configuration.isDemoMode());
        mainActivityRule.getScenario().onActivity(activity -> configuration.setDemoMode(activity, true));
        Class<?> selected2 = configuration.demoModeSelector(Dummy1.class, Dummy2.class);
        assertEquals(selected2, Dummy2.class);
        assertTrue(configuration.isDemoMode());
    }

    @After
    public void release() {
        Intents.release();
    }*/
}
