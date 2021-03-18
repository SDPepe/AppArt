package ch.epfl.sdp.appart.ui.scrolling;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.MockDataBase;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.scrolling.ad.AnnounceActivity;
import ch.epfl.sdp.appart.vtour.VirtualTourActivity;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@UninstallModules(FireBaseModule.class)
@HiltAndroidTest
public class AdUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule  = new HiltAndroidRule(this);

    /*@Rule(order = 1)
    public ActivityScenario<AnnounceActivity> announceActivityRule =
            new ActivityScenarioRule<>(AnnounceActivity.class);
*/
    @BindValue
    Database database = new MockDataBase();

    /**
     * taken from :
     * https://stackoverflow.com/questions/29378552/in-espresso-how-to-avoid-ambiguousviewmatcherexception-when-multiple-views-matc
     * Allows to select the index th view.
     *
     * @param matcher the matcher on the view
     * @param index   the index of the view we want to match
     * @return a Matcher on the View
     */
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void clickOnVTourOpensVTourActivity(){
        Intents.init();
        onView(withId(R.id.vtourButton)).perform(click());
        intended(hasComponent(VirtualTourActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void clickOnInfoOpensContactDialog(){
        onView(withId(R.id.contactInfoButton)).perform(click());
        onView(withId(R.id.dialogView)).check(matches(isDisplayed()));
    }

}
