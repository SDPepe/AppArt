package ch.epfl.sdp.appart;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class AdCreationUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<AdCreationActivity> scrollingActivityRule = new ActivityScenarioRule<>(AdCreationActivity.class);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @Before
    public void init(){

    }



    @After
    public void release(){

    }

}
