package ch.epfl.sdp.appart;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class PictureImportUITest {

    @Rule(order = 0)
    public ActivityScenarioRule<PicturesImportActivity> scrollingActivityRule = new ActivityScenarioRule<>(PicturesImportActivity.class);


    @Before
    public void init() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void ImportButtonRedirectsToAdCreationActivity() {
        ViewInteraction importButtonInteraction = onView(withId(R.id.finish_PictureImport_button));
        importButtonInteraction.perform(click());
        intended(hasComponent(AdCreationActivity.class.getName()));
    }

    @Test
    public void AddTwoCardsAndSwapThem() {

    }
}
