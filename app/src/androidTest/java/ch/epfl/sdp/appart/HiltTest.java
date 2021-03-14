package ch.epfl.sdp.appart;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public final class HiltTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Inject
    Database database;

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void happyPath() {
        // Can already use analyticsAdapter here.
    }


}
