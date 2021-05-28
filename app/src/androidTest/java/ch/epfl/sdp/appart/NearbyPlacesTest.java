package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.hilt.PlaceModule;
import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
import ch.epfl.sdp.appart.place.PlaceService;
import ch.epfl.sdp.appart.place.helper.MockPlaceServiceHelper;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import androidx.test.espresso.contrib.RecyclerViewActions;

@HiltAndroidTest
@UninstallModules({PlaceModule.class})
public class NearbyPlacesTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    static final Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), PlaceActivity.class);
        intent.putExtra(ActivityCommunicationLayout.AD_ADDRESS, "ok rue 1, 1400 Yverdon");
    }

    @Rule(order = 1)
    public ActivityScenarioRule<PlaceActivity> placeActivityRule = new ActivityScenarioRule<>(intent);

    private final Context context =
            InstrumentationRegistry.getInstrumentation().getTargetContext();

    @BindValue
    public final PlaceService placeService = new PlaceService(new MockPlaceServiceHelper(context), new MockGeocodingService());

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void selectItemsShowsList() {
        onView(withId(R.id.spinner_place_activity)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("gym"))).perform(click());
        onView(withId(R.id.places_Place_recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, RecyclerViewItemViewAction.clickChildViewWithId(0)));
        int i = 0;
    }

    //From https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    public static class RecyclerViewItemViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }

}
