package ch.epfl.sdp.appart;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class PanoramaUIImageLoadFailedTest {

    static final String testId = "1PoUWbeNHvMNotxwAui5";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), PanoramaActivity.class);
        ArrayList<String> images = new ArrayList<>();
        images.add("does_not_exists.jpg");
        intent.putStringArrayListExtra(AdActivity.Intents.INTENT_PANORAMA_PICTURES, images);
        intent.putExtra(AdActivity.Intents.INTENT_AD_ID, "dummy");
    }

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<PanoramaActivity> panoramaActivityRule = new ActivityScenarioRule<>(intent);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @Test
    public void checkLoadImageFailed() {
        panoramaActivityRule.getScenario().onActivity(activity -> {
            activity.hasCurrentImageLoadingFailed().thenAccept(s -> assertThat(s, is(false)));
        });
    }


}
