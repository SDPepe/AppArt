package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.List;

import ch.epfl.sdp.appart.panorama.PictureCardAdapter;
import ch.epfl.sdp.appart.panorama.SwapNotifiable;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public class PictureImportUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<PicturesImportActivity> pictureImportActivityRule = new ActivityScenarioRule<>(PicturesImportActivity.class);

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @After
    public void release() {
        Intents.release();
    }

    /*
    @Test
    public void ImportButtonRedirectsToAdCreationActivity() {
        ViewInteraction importButtonInteraction = onView(withId(R.id.finish_PictureImport_button));
        importButtonInteraction.perform(click());
        intended(hasComponent(AdCreationActivity.class.getCanonicalName()));
    }*/

    @Test
    public void AddTwoCardsAndSwapThem() {
        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                Uri uri1 = Uri.fromFile(new File("uri1"));
                Uri uri2 = Uri.fromFile(new File("uri2"));
                activity.addPictureToAdapterAndNotify(uri1);
                activity.addPictureToAdapterAndNotify(uri2);
            }
        });

        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                List<Uri> uris = activity.getOrderedPictures();
                assertEquals("2 uris should be present", 2, uris.size());
            }
        });

        //ViewInteraction upButtonInteraction = onView(ViewUtils.withIndex(withId(R.id.up_button_card_PictureImport), 0));
        //upButtonInteraction.perform(click());

        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                SwapNotifiable adapter = activity.getAdapter();
                adapter.swapFromIndexWithAbove(1);
            }
        });

        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                List<Uri> uris = activity.getOrderedPictures();
                String firstUri = uris.get(0).getPath();
                String secondUri = uris.get(1).getPath();
                assertEquals("first uri must be the second one", "/uri2", firstUri);
                assertEquals("second uri must be the first one", "/uri1", secondUri);
            }
        });

        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                SwapNotifiable adapter = activity.getAdapter();
                adapter.swapFromIndexWithBellow(0);
            }
        });

        pictureImportActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<PicturesImportActivity>() {
            @Override
            public void perform(PicturesImportActivity activity) {
                List<Uri> uris = activity.getOrderedPictures();
                String firstUri = uris.get(0).getPath();
                String secondUri = uris.get(1).getPath();
                assertEquals("first uri must be the first one", "/uri1", firstUri);
                assertEquals("second uri must be the second one", "/uri2", secondUri);
            }
        });

    }
}
