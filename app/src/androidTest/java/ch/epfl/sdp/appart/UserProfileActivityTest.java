package ch.epfl.sdp.appart;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@HiltAndroidTest
public class UserProfileActivityTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<UserProfileActivity> mActivityTestRule = new ActivityScenarioRule<>(UserProfileActivity.class);

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

    @Test
    public void userProfileActivityTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.modifyButton), withText("EDIT PROFILE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("carlo.musso"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("carlo.musso"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText2.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("carlo.musso"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("AppArt test"));

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("AppArt test"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText4.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.age_UserProfile_editText),
                        childAtPosition(
                                allOf(withId(R.id.age_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                1)),
                                1),
                        isDisplayed()));
        textInputEditText5.perform(replaceText("100"), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText),
                        childAtPosition(
                                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                2)),
                                1),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("+39 3333333333"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                        childAtPosition(
                                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                2)),
                                1),
                        isDisplayed()));
        textInputEditText7.perform(pressImeActionButton());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.gender_UserProfile_spinner),
                        childAtPosition(
                                allOf(withId(R.id.gender_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                3)),
                                1),
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
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.emailTitle_ContactInfo_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.emailTitle_ContactInfo_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Email")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.emailText_UserProfile_textView), withText("carlo.musso@epfl.ch"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.emailText_UserProfile_textView), withText("carlo.musso@epfl.ch"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("carlo.musso@epfl.ch")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profilePicture_UserProfile_imageView), withContentDescription("profile picture"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.uniAccountClaimer_UserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.uniAccountClaimer_UserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView6.check(matches(withText("UNI account")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.name_UserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.name_UserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView8.check(matches(withText("Name")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("AppArt test"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("AppArt test"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText2.check(matches(withText("AppArt test")));

        ViewInteraction linearLayout9 = onView(
                allOf(withId(R.id.age_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout9.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.age_UserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.age_UserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView10.check(matches(withText("Age")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.age_UserProfile_editText), withText("100"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.age_UserProfile_editText), withText("100"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText4.check(matches(withText("100")));

        ViewInteraction linearLayout10 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout10.check(matches(isDisplayed()));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView11.check(matches(withText("Phone")));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView12.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText5.check(matches(isDisplayed()));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText6.check(matches(withText("+39 3333333333")));

        ViewInteraction linearLayout11 = onView(
                allOf(withId(R.id.gender_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout11.check(matches(isDisplayed()));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.gender_UserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView13.check(matches(isDisplayed()));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.gender_UserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView14.check(matches(withText("Gender")));

        ViewInteraction spinner = onView(
                allOf(withId(R.id.gender_UserProfile_spinner),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        spinner.check(matches(isDisplayed()));

        ViewInteraction textView15 = onView(
                allOf(withId(android.R.id.text1), withText("Male"),
                        withParent(allOf(withId(R.id.gender_UserProfile_spinner),
                                withParent(withId(R.id.gender_UserProfile_layout)))),
                        isDisplayed()));
        textView15.check(matches(withText("Male")));

        ViewInteraction button = onView(
                allOf(withId(R.id.modifyButton), withText("EDIT PROFILE"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.back_UserProfile_button), withText("BACK"),
                        withParent(allOf(withId(R.id.infoLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.back_UserProfile_button), withText("BACK"),
                        withParent(allOf(withId(R.id.infoLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));
    }
}
