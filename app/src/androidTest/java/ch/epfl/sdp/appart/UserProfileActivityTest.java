package ch.epfl.sdp.appart;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class UserProfileActivityTest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    /* Used to grant camera permission always */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @BindValue
    LoginService login = new MockLoginService();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }
    @Test
    public void userProfileActivityTest() throws UiObjectNotFoundException {
        ViewInteraction appCompatEditText = onView(
            allOf(withId(R.id.email_Login_editText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    0),
                isDisplayed()));
        appCompatEditText.perform(replaceText("carlo@epfl.ch"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
            allOf(withId(R.id.password_Login_editText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    1),
                isDisplayed()));
        appCompatEditText2.perform(replaceText("3333"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
            allOf(withId(R.id.login_Login_button), withText("Log In!"),
                isDisplayed()));
        appCompatButton.perform(click());

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

        ViewInteraction appCompatButton2 = onView(
            allOf(withId(R.id.editProfile_UserProfile_button), withText("Edit profile"),
                childAtPosition(
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        1),
                    1),
                isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textInputEditText = onView(
            allOf(withId(R.id.name_UserProfile_editText), withText("carlo"),
                childAtPosition(
                    allOf(withId(R.id.name_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            0)),
                    1),
                isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(
            allOf(withId(R.id.name_UserProfile_editText), withText("carlo"),
                childAtPosition(
                    allOf(withId(R.id.name_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            0)),
                    1),
                isDisplayed()));
        textInputEditText2.perform(replaceText("TEST"));

        ViewInteraction textInputEditText3 = onView(
            allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                childAtPosition(
                    allOf(withId(R.id.name_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            0)),
                    1),
                isDisplayed()));
        textInputEditText3.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText4 = onView(
            allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                childAtPosition(
                    allOf(withId(R.id.name_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            0)),
                    1),
                isDisplayed()));
        textInputEditText4.perform(pressImeActionButton());

        ViewInteraction textInputEditText5 = onView(
            allOf(withId(R.id.age_UserProfile_editText), withText(""),
                childAtPosition(
                    allOf(withId(R.id.age_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            1)),
                    1),
                isDisplayed()));
        textInputEditText5.perform(replaceText("1000"));

        ViewInteraction textInputEditText6 = onView(
            allOf(withId(R.id.age_UserProfile_editText), withText("1000"),
                childAtPosition(
                    allOf(withId(R.id.age_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            1)),
                    1),
                isDisplayed()));
        textInputEditText6.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
            allOf(withId(R.id.age_UserProfile_editText), withText("1000"),
                childAtPosition(
                    allOf(withId(R.id.age_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            1)),
                    1),
                isDisplayed()));
        textInputEditText7.perform(pressImeActionButton());

        ViewInteraction textInputEditText8 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_editText),
                childAtPosition(
                    allOf(withId(R.id.phoneNumber_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            2)),
                    1),
                isDisplayed()));
        textInputEditText8.perform(replaceText("+39 3333333333"));

        ViewInteraction textInputEditText9 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                childAtPosition(
                    allOf(withId(R.id.phoneNumber_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            2)),
                    1),
                isDisplayed()));
        textInputEditText9.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText10 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                childAtPosition(
                    allOf(withId(R.id.phoneNumber_UserProfile_layout),
                        childAtPosition(
                            withId(R.id.attributes_UserProfile_layout),
                            2)),
                    1),
                isDisplayed()));
        textInputEditText10.perform(pressImeActionButton());

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
            .atPosition(3);
        appCompatTextView2.perform(click());



        onView(withId(R.id.editImage_UserProfile_button)).perform(click());



        /* =================================================================================================== */
        /*                            CALL THE CAMERA AND RECEIVE A MOCK IMAGE BACK                            */
        /* =================================================================================================== */

        int initialDatabaseImageSize = ((MockDatabaseService) database).getImages().size();


        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
            ApplicationProvider.getApplicationContext().getResources(),
            R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(ActivityCommunicationLayout.RESULT_IS_FOR_TEST, resultIntent);

        // When an intent is sent to the Camera, this tells Espresso to respond with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);


        // click on the button in our app that launches into the Camera
        onView(withId(R.id.camera_Camera_button)).perform(click());

        // validate that an intent resolving to the "camera" activity has been sent out by app
        intended(toPackage("com.android.camera2"));

        // Initialize UiDevice instance
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Search for correct button in the dialog.
        UiObject buttonAllow = uiDevice.findObject(new UiSelector().text("ALLOW"));

        if (buttonAllow.exists() && buttonAllow.isEnabled()) {
            buttonAllow.click();
            uiDevice.pressBack();
        }

        // Search for correct button in the dialog.
        UiObject buttonAllow2 = uiDevice.findObject(new UiSelector().text("Allow all the time"));

        if (buttonAllow2.exists() && buttonAllow2.isEnabled()) {
            buttonAllow2.click();
            uiDevice.pressBack();
        }

        ViewInteraction appCompatButton4 = onView(
            allOf(withId(R.id.confirm_Camera_button), withText("Confirm"),
                isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
            allOf(withId(R.id.doneEditing_UserProfile_button), withText("Confirm"),
                childAtPosition(
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        1),
                    0),
                isDisplayed()));
        appCompatButton5.perform(click());

        /* after the done button the previous image should have been removed and the new one updated */
        List<String> mockImages =  ((MockDatabaseService) database).getImages();

        assertThat(mockImages.size(), is(initialDatabaseImageSize ));
        assertFalse(mockImages.contains("users/default/user_example_no_gender.png"));
        /* contains has to be used since the exact name of the image depends on System.currentTimeMillis() */
        assertTrue(mockImages.get(mockImages.size() - 1).contains("users/3333/profileImage"));


        ViewInteraction appCompatButton6 = onView(
            allOf(withId(R.id.editProfile_UserProfile_button), withText("Edit profile"),
                childAtPosition(
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        1),
                    1),
                isDisplayed()));
        appCompatButton6.perform(click());


        onView(withId(R.id.removeImage_UserProfile_button)).perform(click());

        ViewInteraction appCompatButton8 = onView(
            allOf(withId(R.id.doneEditing_UserProfile_button), withText("Confirm"),
                childAtPosition(
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        1),
                    0),
                isDisplayed()));
        appCompatButton8.perform(click());

        mockImages = ((MockDatabaseService) database).getImages();

        /* the image is removed */
        assertThat(mockImages.size(), is(initialDatabaseImageSize - 1));

        /* contains has to be used since the exact name of the image depends on System.currentTimeMillis() */
        assertFalse(mockImages.get(mockImages.size() - 1).contains("users/3333/profileImage"));

        ViewInteraction textView = onView(
            allOf(withId(R.id.email_UserProfile_textView), withText("Email"),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        textView.check(matches(withText("Email")));

        ViewInteraction textView2 = onView(
            allOf(withId(R.id.emailText_UserProfile_textView), withText("carlo@epfl.ch"),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        textView2.check(matches(withText("carlo@epfl.ch")));

        ViewInteraction imageView = onView(
            allOf(withId(R.id.profilePicture_UserProfile_imageView), withContentDescription("profile picture"),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
            allOf(withId(R.id.uniAccountClaimer_UserProfile_textView), withText("UNI account"),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        textView3.check(matches(withText("UNI account")));

        ViewInteraction linearLayout = onView(
            allOf(withId(R.id.attributes_UserProfile_layout),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
            allOf(withId(R.id.name_UserProfile_layout),
                withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                    withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
            allOf(withId(R.id.name_UserProfile_textView), withText("Name"),
                withParent(allOf(withId(R.id.name_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        textView4.check(matches(withText("Name")));

        ViewInteraction editText = onView(
            allOf(withId(R.id.name_UserProfile_editText), withText("TEST"),
                withParent(allOf(withId(R.id.name_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        editText.check(matches(withText("TEST")));

        ViewInteraction linearLayout3 = onView(
            allOf(withId(R.id.age_UserProfile_layout),
                withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                    withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
            allOf(withId(R.id.age_UserProfile_textView), withText("Age"),
                withParent(allOf(withId(R.id.age_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        textView5.check(matches(withText("Age")));

        ViewInteraction editText2 = onView(
            allOf(withId(R.id.age_UserProfile_editText), withText("1000"),
                withParent(allOf(withId(R.id.age_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        editText2.check(matches(withText("1000")));

        ViewInteraction linearLayout4 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_layout),
                withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                    withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                isDisplayed()));
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_textView), withText("Phone"),
                withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        textView6.check(matches(withText("Phone")));

        ViewInteraction editText3 = onView(
            allOf(withId(R.id.phoneNumber_UserProfile_editText), withText("+39 3333333333"),
                withParent(allOf(withId(R.id.phoneNumber_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        editText3.check(matches(withText("+39 3333333333")));

        ViewInteraction linearLayout5 = onView(
            allOf(withId(R.id.gender_UserProfile_layout),
                withParent(allOf(withId(R.id.attributes_UserProfile_layout),
                    withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                isDisplayed()));
        linearLayout5.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
            allOf(withId(R.id.gender_UserProfile_textView), withText("Gender"),
                withParent(allOf(withId(R.id.gender_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        textView7.check(matches(withText("Gender")));

        ViewInteraction spinner = onView(
            allOf(withId(R.id.gender_UserProfile_spinner),
                withParent(allOf(withId(R.id.gender_UserProfile_layout),
                    withParent(withId(R.id.attributes_UserProfile_layout)))),
                isDisplayed()));
        spinner.check(matches(isDisplayed()));

        ViewInteraction button = onView(
            allOf(withId(R.id.editProfile_UserProfile_button), withText("Edit profile"),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
        button.check(matches(isDisplayed()));

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
