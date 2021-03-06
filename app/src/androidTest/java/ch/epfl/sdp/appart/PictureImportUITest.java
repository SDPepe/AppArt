package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.List;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.panorama.SwapNotifiable;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@HiltAndroidTest
@UninstallModules(DatabaseModule.class)
public class PictureImportUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule(order = 2)
    public ActivityScenarioRule<PicturesImportActivity> pictureImportActivityRule = new ActivityScenarioRule<>(PicturesImportActivity.class);

    @BindValue
    DatabaseService db = new MockDatabaseService();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @After
    public void release() {
        Intents.release();
    }

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

    @Test
    public void addButtonStartOpenDocument(){
        onView(withId(R.id.add_PictureImport_button)).perform(click());
        intended(hasAction(Intent.ACTION_OPEN_DOCUMENT));
    }

    @Test
    public void importButtonFinishes() {
        onView(withId(R.id.finish_PictureImport_button)).perform(click());
        assertEquals(pictureImportActivityRule.getScenario().getResult().getResultCode(), RESULT_OK);
    }
}
