package ch.epfl.sdp.appart;


import android.view.View;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@HiltAndroidTest
public class SimpleUserProfileActivityTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<SimpleUserProfileActivity> mActivityTestRule = new ActivityScenarioRule<>(SimpleUserProfileActivity.class);

    @Test
    public void simpleUserProfileActivityTest() {
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
                allOf(withId(R.id.emailText_SimpleUserProfile_textView), withText("marie.bernard@gmail.com"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profilePicture_SimpleUserProfile_imageView), withContentDescription("profile picture"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.uniAccountClaimer_SimpleUserProfile_textView), withText("Regular account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

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

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_textView), withText("Name"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView6.check(matches(withText("Name")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.name_SimpleUserProfile_editText), withText("Marie Bernard"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_textView), withText("Age"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView8.check(matches(withText("Age")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_editText), withText("25"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_textView), withText("Phone"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView10.check(matches(withText("Phone")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_editText), withText("+41 7666666666"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction linearLayout5 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_layout),
                        withParent(allOf(withId(R.id.attributes_SimpleUserProfile_layout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        linearLayout5.check(matches(isDisplayed()));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView11.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_textView), withText("Gender"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        textView12.check(matches(withText("Gender")));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_editText), withText("FEMALE"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText4.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.gender_SimpleUserProfile_editText), withText("FEMALE"),
                        withParent(allOf(withId(R.id.gender_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText5.check(matches(withText("FEMALE")));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.phoneNumber_SimpleUserProfile_editText), withText("+41 7666666666"),
                        withParent(allOf(withId(R.id.phoneNumber_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText6.check(matches(withText("+41 7666666666")));

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.age_SimpleUserProfile_editText), withText("25"),
                        withParent(allOf(withId(R.id.age_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText7.check(matches(withText("25")));

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.name_SimpleUserProfile_editText), withText("Marie Bernard"),
                        withParent(allOf(withId(R.id.name_SimpleUserProfile_layout),
                                withParent(withId(R.id.attributes_SimpleUserProfile_layout)))),
                        isDisplayed()));
        editText8.check(matches(withText("Marie Bernard")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.emailText_SimpleUserProfile_textView), withText("marie.bernard@gmail.com"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView13.check(matches(withText("marie.bernard@gmail.com")));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.uniAccountClaimer_SimpleUserProfile_textView), withText("Regular account"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView14.check(matches(withText("Regular account")));
    }
}
