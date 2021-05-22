package ch.epfl.sdp.appart;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class FullScreenImageUITest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), FullScreenImageActivity.class);
        intent.putExtra("imageId", "file:///android_asset/panorama_test.jpg");
    }

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<FullScreenImageActivity> fullscreenActivityRule =
            new ActivityScenarioRule<>(intent);

    @BindValue
    DatabaseService db = new MockDatabaseService();

    @Test
    public void imageVisibilityTest(){
        onView(withId(R.id.image_FullScreenImage_imageView))
                .perform(click())
                .check(matches(isDisplayed()));
    }

}
