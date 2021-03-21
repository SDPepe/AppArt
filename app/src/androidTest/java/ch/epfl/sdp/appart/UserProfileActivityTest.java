package ch.epfl.sdp.appart;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.user.UserProfileActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest {

    @Rule
    public ActivityTestRule<UserProfileActivity> mActivityTestRule = new ActivityTestRule<>(UserProfileActivity.class);

    @Test
    public void userProfileActivityTester() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.modifyButton), withText("EDIT PROFILE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.nameText), withText("carlo.musso"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        textInputEditText.perform(replaceText("AppArt"));

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.nameText), withText("AppArt"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        textInputEditText2.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.nameText), withText("AppArt"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        textInputEditText3.perform(pressImeActionButton());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.ageText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("22"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.ageText), withText("22"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        textInputEditText5.perform(pressImeActionButton());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.phoneNumberText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("+393333333333"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.phoneNumberText), withText("+393333333333"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        textInputEditText7.perform(pressImeActionButton());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.genderView),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatTextView.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.doneButton), withText("DONE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.emailText), withText("carlo.musso@epfl.ch"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(withText("carlo.musso@epfl.ch")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.nameText), withText("AppArt"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText.check(matches(withText("AppArt")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.ageText), withText("22"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText2.check(matches(withText("22")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.phoneNumberText), withText("+393333333333"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText3.check(matches(withText("+393333333333")));

        ViewInteraction spinner = onView(
                allOf(withId(R.id.genderView),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        spinner.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.backButton), withText("BACK"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.modifyButton), withText("EDIT PROFILE"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.imageView),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.uniAccounClaimer), withText("UNI account"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView2.check(matches(withText("UNI account")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.uniAccounClaimer), withText("UNI account"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.emailText), withText("carlo.musso@epfl.ch"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.nameText), withText("AppArt"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText4.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.ageText), withText("22"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText5.check(matches(isDisplayed()));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.phoneNumberText), withText("+393333333333"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText6.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.email), withText("Email"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.email), withText("Email"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView6.check(matches(withText("Email")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.name), withText("Name"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.name), withText("Name"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView8.check(matches(withText("Name")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.age), withText("Age"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.age), withText("Age"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView10.check(matches(withText("Age")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.phoneNumber), withText("Phone"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView11.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.phoneNumber), withText("Phone"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView12.check(matches(withText("Phone")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.gender), withText("Gender"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView13.check(matches(isDisplayed()));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.gender), withText("Gender"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView14.check(matches(withText("Gender")));

        ViewInteraction textView15 = onView(
                allOf(withId(android.R.id.text1), withText("Male"),
                        withParent(allOf(withId(R.id.genderView),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView15.check(matches(withText("Male")));

        ViewInteraction textView16 = onView(
                allOf(withId(android.R.id.text1), withText("Male"),
                        withParent(allOf(withId(R.id.genderView),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView16.check(matches(withText("Male")));
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
}
