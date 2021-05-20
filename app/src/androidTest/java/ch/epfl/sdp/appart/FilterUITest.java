package ch.epfl.sdp.appart;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@UninstallModules({DatabaseModule.class})
@HiltAndroidTest
public class FilterUITest {
  @Rule(order = 0)
  public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
  @Rule(order = 1)
  public ActivityScenarioRule<ScrollingActivity> mActivityTestRule = new ActivityScenarioRule<>(ScrollingActivity.class);
  @BindValue
  DatabaseService database = new MockDatabaseService();
  @Before
  public void init() {
    Intents.init();
    hiltRule.inject();
  }
  @Test
  public void filterUITest() {

    ViewInteraction button = onView(
        allOf(withId(R.id.filter_Scrolling_button),
            withParent(withParent(withId(R.id.columnLayout_Scrolling_LinearLayout))),
            isDisplayed()));
    button.check(matches(isDisplayed()));

    ViewInteraction appCompatButton2 = onView(
        allOf(withId(R.id.filter_Scrolling_button),
            childAtPosition(
                childAtPosition(
                    withId(R.id.columnLayout_Scrolling_LinearLayout),
                    0),
                2),
            isDisplayed()));
    appCompatButton2.perform(click());

    ViewInteraction appCompatEditText = onView(
        allOf(withId(R.id.value_min_price__Filter_editText),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    1),
                0),
            isDisplayed()));
    appCompatEditText.perform(replaceText("0"), closeSoftKeyboard());

    ViewInteraction appCompatEditText2 = onView(
        allOf(withId(R.id.value_max_price_Filter_editText),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    1),
                0),
            isDisplayed()));
    appCompatEditText2.perform(replaceText("100"), closeSoftKeyboard());

    ViewInteraction editText = onView(
        allOf(withId(R.id.value_min_price__Filter_editText), withText("0"),
            withParent(
                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
            isDisplayed()));
    editText.check(matches(withText("0")));

    ViewInteraction editText2 = onView(
        allOf(withId(R.id.value_max_price_Filter_editText), withText("100"),
            withParent(
                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
            isDisplayed()));
    editText2.check(matches(withText("100")));


    ViewInteraction button2 = onView(
        allOf(withId(R.id.clear_Filter_button), withText("Remove all"),
            withParent(withParent(withId(android.R.id.content))),
            isDisplayed()));
    button2.check(matches(isDisplayed()));

    ViewInteraction button3 = onView(
        allOf(withId(R.id.confirm_Filter_button), withText("Save"),
            withParent(withParent(withId(android.R.id.content))),
            isDisplayed()));
    button3.check(matches(isDisplayed()));

    ViewInteraction appCompatButton3 = onView(
        allOf(withId(R.id.confirm_Filter_button), withText("Save"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                8),
            isDisplayed()));
    appCompatButton3.perform(click());

    ViewInteraction appCompatButton4 = onView(
        allOf(withId(R.id.filter_Scrolling_button),
            childAtPosition(
                childAtPosition(
                    withId(R.id.columnLayout_Scrolling_LinearLayout),
                    0),
                2),
            isDisplayed()));
    appCompatButton4.perform(click());

    ViewInteraction appCompatButton5 = onView(
        allOf(withId(R.id.clear_Filter_button), withText("Remove all"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                7),
            isDisplayed()));
    appCompatButton5.perform(click());
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

  @After
  public void release() {
    Intents.release();
  }
}
