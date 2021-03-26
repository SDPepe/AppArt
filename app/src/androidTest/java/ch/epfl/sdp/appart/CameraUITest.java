package ch.epfl.sdp.appart;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import ch.epfl.sdp.appart.scrolling.CameraActivity;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@HiltAndroidTest
public class CameraUITest {


  @Rule(order = 0)
  public HiltAndroidRule hiltRule  = new HiltAndroidRule(this);

  @Rule(order = 1)
  public ActivityScenarioRule<CameraActivity> intentsRule = new ActivityScenarioRule<>(CameraActivity.class);

  @Before
  public void init() {
    hiltRule.inject();
  }

  @Test
  public void clickOnCameraBtn(){
    Intents.init();
    onView(withId(R.id.button_camera)).perform(click());
    Intents.release();
  }

  @Test
  public void clickOnGalleryBtn(){
    Intents.init();
    onView(withId(R.id.button_gallery)).perform(click());
    Intents.release();
  }
  

}
