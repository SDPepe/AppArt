package ch.epfl.sdp.appart;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static android.app.Activity.RESULT_CANCELED;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class SimpleUserProfileActivityTest {
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

    public static ViewAction forceClick() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isClickable();
            }

            @Override public String getDescription() {
                return "force click";
            }

            @Override public void perform(UiController uiController, View view) {
                view.performClick(); // perform click without checking view coordinates.
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    @Test
    public void simpleUserProfileActivityTest() {
        ViewInteraction appCompatImageView = onView(
            ViewUtils.withIndex(withId(R.id.image_CardLayout_imageView),
                0));
        appCompatImageView.perform(forceClick());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.contact_info_Ad_button), withText("Contact"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                16)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.email_SimpleUserProfile_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.email_SimpleUserProfile_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Email")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.emailText_SimpleUserProfile_textView), withText("vetterli@epfl.ch"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.emailText_SimpleUserProfile_textView), withText("vetterli@epfl.ch"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("vetterli@epfl.ch")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profilePicture_SimpleUserProfile_imageView), withContentDescription("profile picture"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.uniAccountClaimer_SimpleUserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.uniAccountClaimer_SimpleUserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView6.check(matches(withText("UNI account")));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView7.check(matches(withText("Name")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView8.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withId(R.id.name_SimpleUserProfile_editText), withText("Martin Vetterli"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_editText), withText("Martin Vetterli"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText2.check(matches(withText("Martin Vetterli")));

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView10.check(matches(withText("Age")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_editText), withText("40"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_editText), withText("40"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText4.check(matches(withText("40")));

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView11.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView12.check(matches(withText("Phone")));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_editText), withText("0777777777"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText5.check(matches(isDisplayed()));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_editText), withText("0777777777"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText6.check(matches(withText("0777777777")));

        ViewInteraction linearLayout5 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout5.check(matches(isDisplayed()));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView13.check(matches(isDisplayed()));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView14.check(matches(withText("Gender")));

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_editText), withText("MALE"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText7.check(matches(isDisplayed()));

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_editText), withText("MALE"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText8.check(matches(withText("MALE")));

        ViewInteraction button = onView(
                allOf(withId(R.id.contact_SimpleUserProfile_button), withText("CONTACT ANNOUNCER"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void backButtonPressedTest(){
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
        assertEquals(mActivityTestRule.getScenario().getResult().getResultCode(), RESULT_CANCELED);
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

    @After
    public void release() {
        Intents.release();
    }
}
