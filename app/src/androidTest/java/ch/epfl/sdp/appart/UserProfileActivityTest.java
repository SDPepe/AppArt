package ch.epfl.sdp.appart;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

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
@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class UserProfileActivityTest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @BindValue
    LoginService login = new MockLoginService();

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }
    @Test
    public void userProfileActivityTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.email_Login_editText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("carlo@epfl.ch"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password_Login_editText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("3333"), closeSoftKeyboard());

        onView(withId(R.id.login_Login_button)).perform(click());

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_Scrolling_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

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
                allOf(withId(R.id.name_UserProfile_editText), withText("carlo"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText.perform(replaceText("TEST"));

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText2.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                        childAtPosition(
                                allOf(withId(R.id.name_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textInputEditText3.perform(pressImeActionButton());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.age_UserProfile_editText), withText(""),
                        childAtPosition(
                                allOf(withId(R.id.age_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                1)),
                                1),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("100"));

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.age_UserProfile_editText), withText("100"),
                        childAtPosition(
                                allOf(withId(R.id.age_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                1)),
                                1),
                        isDisplayed()));
        textInputEditText5.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.age_UserProfile_editText), withText("100"),
                        childAtPosition(
                                allOf(withId(R.id.age_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                1)),
                                1),
                        isDisplayed()));
        textInputEditText6.perform(pressImeActionButton());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText),
                        childAtPosition(
                                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                2)),
                                1),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("+39 3333333333"), closeSoftKeyboard());

        ViewInteraction textInputEditText8 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                        childAtPosition(
                                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                        childAtPosition(
                                                withId(R.id.attributes_UserProfile_layout),
                                                2)),
                                1),
                        isDisplayed()));
        textInputEditText8.perform(pressImeActionButton());

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

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(0);
        appCompatTextView2.perform(click());

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
                allOf(withId(R.id.email_UserProfile_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.email_UserProfile_textView), withText("Email"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Email")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.emailText_UserProfile_textView), withText("carlo@epfl.ch"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.emailText_UserProfile_textView), withText("carlo@epfl.ch"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("carlo@epfl.ch")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profilePicture_UserProfile_imageView), withContentDescription("profile picture"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.uniAccountClaimer_UserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.uniAccountClaimer_UserProfile_textView), withText("UNI account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView6.check(matches(withText("UNI account")));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.name_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.name_UserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                        withParent(allOf(withId(R.id.name_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        editText2.check(matches(withText("TEST")));

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.age_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.age_UserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView8.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.age_UserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView9.check(matches(withText("Age")));

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

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView10.check(matches(isDisplayed()));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.phoneNumber_UserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView11.check(matches(withText("Phone")));

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

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.gender_UserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction spinner = onView(
                allOf(withId(R.id.gender_UserProfile_spinner),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        spinner.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.gender_UserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView12.check(matches(isDisplayed()));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.gender_UserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_UserProfile_layout),
                                withParent(withId(R.id.attributes_UserProfile_layout)))),
                        isDisplayed()));
        textView13.check(matches(withText("Gender")));

        ViewInteraction button = onView(
                allOf(withId(R.id.modifyButton), withText("EDIT PROFILE"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.back_UserProfile_button), withText("BACK"),
                        withParent(allOf(withId(R.id.infoLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
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
