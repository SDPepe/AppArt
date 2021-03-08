package ch.epfl.sdp.appart.ui.scrolling;

import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ScrollingUITest {

    @Rule
    public ActivityScenarioRule<ScrollingActivity> scrollingActivityRule = new ActivityScenarioRule<>(ScrollingActivity.class);

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
        onView(withIndex(withId(R.id.card_image), 0)).perform(click());
        Intents.release();
    }

}
