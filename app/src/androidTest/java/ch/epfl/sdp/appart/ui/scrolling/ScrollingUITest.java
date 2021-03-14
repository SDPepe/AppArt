package ch.epfl.sdp.appart.ui.scrolling;

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
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.MockDataBase;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


//@RunWith(AndroidJUnit4.class)
@UninstallModules(FireBaseModule.class)
@HiltAndroidTest
public class ScrollingUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<ScrollingActivity> scrollingActivityRule = new ActivityScenarioRule<>(ScrollingActivity.class);

    @BindValue Database database = new MockDataBase();

    @Before
    public void init() {
        hiltRule.inject();
    }

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

    @Test
    public void clickOnImageViewFromCardViewStartAnnounceActivity() {


        Intents.init();
        //onView(withId(R.id.card_image)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        ViewInteraction card = onView(withIndex(withId(R.id.card_image), 0));
        card.perform(click());

        Intents.release();

    }

}
